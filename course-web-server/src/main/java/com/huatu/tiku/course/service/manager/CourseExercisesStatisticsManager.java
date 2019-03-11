package com.huatu.tiku.course.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.common.exception.BizException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.dao.manual.CourseExercisesStatisticsMapper;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.course.ztk.api.v4.user.UserServiceV4;
import com.huatu.tiku.entity.CourseExercisesStatistics;
import com.huatu.ztk.paper.bean.PracticeCard;
import com.huatu.ztk.paper.bean.PracticeForCoursePaper;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-10 4:22 PM
 **/

@Slf4j
@Component
public class CourseExercisesStatisticsManager {

    @Autowired
    private CourseExercisesStatisticsMapper courseExercisesStatisticsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserServiceV4 userService;

    @Async
    public synchronized void dealCourseExercisesStatistics(PracticeCard answerCard) throws BizException{
        try{
            PracticeForCoursePaper practiceForCoursePaper = (PracticeForCoursePaper)answerCard.getPaper();
            String key = CourseCacheKey.getCourseWorkDealData(practiceForCoursePaper.getCourseType(), practiceForCoursePaper.getCourseId());
            String rankKey = CourseCacheKey.getCourseWorkRankInfo(practiceForCoursePaper.getCourseType(), practiceForCoursePaper.getCourseId());
            SetOperations<String, String> setOperations = redisTemplate.opsForSet();
            ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
            if(setOperations.isMember(key, String.valueOf(answerCard.getUserId()))){
                return;
            }
            Example example = new Example(CourseExercisesStatistics.class);
            example.and()
                    .andEqualTo("courseId", practiceForCoursePaper.getCourseId())
                    .andEqualTo("courseType", practiceForCoursePaper.getType())
                    .andEqualTo("status", YesOrNoStatus.YES);
            CourseExercisesStatistics courseExercisesStatistics = courseExercisesStatisticsMapper.selectOneByExample(example);

            if(null == courseExercisesStatistics){
                courseExercisesStatistics = new CourseExercisesStatistics();
                courseExercisesStatistics.setStatus(YesOrNoStatus.YES.getCode());
                courseExercisesStatistics.setCorrects(answerCard.getRcount());
                courseExercisesStatistics.setCosts(answerCard.getExpendTime());
                courseExercisesStatistics.setCounts(1);
                courseExercisesStatistics.setCourseType(practiceForCoursePaper.getType());
                courseExercisesStatistics.setCourseId(practiceForCoursePaper.getCourseId());
                courseExercisesStatistics.setGmtModify(new Timestamp(System.currentTimeMillis()));
                courseExercisesStatistics.setGmtCreate(new Timestamp(System.currentTimeMillis()));
                courseExercisesStatisticsMapper.insertSelective(courseExercisesStatistics);
            }else{
                CourseExercisesStatistics update = new CourseExercisesStatistics();
                update.setId(courseExercisesStatistics.getId());
                update.setCounts(courseExercisesStatistics.getCounts() + 1);
                update.setCosts(courseExercisesStatistics.getCosts() + answerCard.getExpendTime());
                update.setCorrects(courseExercisesStatistics.getCorrects() + answerCard.getRcount());
                update.setGmtModify(new Timestamp(System.currentTimeMillis()));
                courseExercisesStatisticsMapper.updateByPrimaryKeySelective(update);
            }
            setOperations.add(key, String.valueOf(answerCard.getUserId()));
            int times = Arrays.stream(answerCard.getTimes()).sum();
            long score = (practiceForCoursePaper.getQcount() - answerCard.getRcount()) * 100000 + times;

            UserRankInfo userRankInfo = UserRankInfo.builder()
                    .uid(answerCard.getUserId())
                    .rcount(answerCard.getRcount())
                    .expendTime(times)
                    .submitTimeInfo(answerCard.getCardCreateTime())
                    .build();

            zSetOperations.add(rankKey, objectMapper.writeValueAsString(userRankInfo), score);
        }catch (Exception e){
            log.error("dealCourseExercisesStatistics caught an error!:{}", e);
        }
    }


    /**
     * 获取课后作业排名统计信息
     * @param practiceCard
     * @return
     * @throws BizException
     */
    public Map<String, Object> obtainCourseRankInfo(PracticeCard practiceCard)throws BizException{
        Map<String, Object> rankInfo = Maps.newHashMap();
        PracticeForCoursePaper practiceForCoursePaper = (PracticeForCoursePaper) practiceCard.getPaper();
        Example example = new Example(CourseExercisesStatistics.class);
        example.and()
                .andEqualTo("courseId", practiceForCoursePaper.getCourseId())
                .andEqualTo("courseType", practiceForCoursePaper.getCourseType())
                .andEqualTo("status", YesOrNoStatus.YES);

        CourseExercisesStatistics courseExercisesStatistics = courseExercisesStatisticsMapper.selectOneByExample(example);

        rankInfo.put("avgTimeCost", courseExercisesStatistics.getCosts() / courseExercisesStatistics.getCounts());
        rankInfo.put("avgCorrect", courseExercisesStatistics.getCorrects() / courseExercisesStatistics.getCounts());
        String rankKey = CourseCacheKey.getCourseWorkRankInfo(practiceForCoursePaper.getCourseType(), practiceForCoursePaper.getCourseId());
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        long myRank = zSetOperations.rank(rankKey, String.valueOf(practiceCard.getUserId())) + 1;
        Set<String> userRankInfos = zSetOperations.range(rankKey, 0, 9);
        List<UserRankInfo> userRankInfoArrayList = Lists.newArrayList();
        userRankInfoArrayList.addAll(userRankInfos.stream().map(item -> {
            UserRankInfo userRankInfo = objectMapper.convertValue(item, UserRankInfo.class);
            return userRankInfo;
        }).collect(Collectors.toList()));

        List<Long> userIds = userRankInfoArrayList.stream().map(UserRankInfo::getUid).collect(Collectors.toList());
        if(!userIds.contains(practiceCard.getUserId())){
            UserRankInfo userRankInfo = UserRankInfo
                    .builder()
                    .uid(practiceCard.getUserId())
                    .submitTimeInfo(practiceCard.getCreateTime())
                    .rcount(practiceCard.getRcount())
                    .expendTime(Arrays.stream(practiceCard.getTimes()).sum())
                    .build();

            userRankInfoArrayList.add(userRankInfoArrayList.size() -1, userRankInfo);
        }

        if(CollectionUtils.isNotEmpty(userRankInfoArrayList)){
            UserRankInfo top = userRankInfoArrayList.get(0);
            rankInfo.put("maxCorrect", top.getRcount());
            rankInfo.put("ranks", dealRanks(userRankInfoArrayList));
        }else{
            rankInfo.put("maxCorrect", 0);
            rankInfo.put("ranks", Lists.newArrayList());
        }
        rankInfo.put("myRank", myRank);
        return rankInfo;
    }


    /**
     * 处理课后作业排名
     * @param userRankInfoArrayList
     * @return
     * @throws BizException
     */
    private List<UserRankInfo> dealRanks(List<UserRankInfo> userRankInfoArrayList) throws BizException{
        List<String> userIds = userRankInfoArrayList.stream().map(item->{
            long uId = item.getUid();
            return String.valueOf(uId);
        }).collect(Collectors.toList());
        NetSchoolResponse netSchoolResponse = userService.getUserLevelBatch(userIds);
        Map<String, Map<String, Object>> userInfoMaps = Maps.newHashMap();
        List<Map<String, Object>> userInfoList = (List<Map<String, Object>>) netSchoolResponse.getData();
        userInfoList.forEach(item -> userInfoMaps.put(String.valueOf(item.get("id")), item));
        userRankInfoArrayList.forEach(userRankInfo ->{
            Map<String,Object> detail = userInfoMaps.get(String.valueOf(userRankInfo.getUid()));
            userRankInfo.setAvatar(String.valueOf(detail.get("avatar")));
            userRankInfo.setUname(String.valueOf(detail.get("nick")));
        });
        return userRankInfoArrayList;
    }


    @NoArgsConstructor
    @Getter
    @Setter
    public static class UserRankInfo implements Serializable{
        private int rank;
        private long uid;
        private String uname;
        private String avatar;
        private int rcount;
        private int expendTime;
        private long submitTimeInfo;

        @Builder
        public UserRankInfo(int rank, long uid, String uname, String avatar, int rcount, int expendTime, long submitTimeInfo) {
            this.rank = rank;
            this.uid = uid;
            this.uname = uname;
            this.avatar = avatar;
            this.rcount = rcount;
            this.expendTime = expendTime;
            this.submitTimeInfo = submitTimeInfo;
        }
    }
}
