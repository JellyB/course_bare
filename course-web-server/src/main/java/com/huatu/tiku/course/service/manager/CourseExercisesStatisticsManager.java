package com.huatu.tiku.course.service.manager;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.common.exception.BizException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private UserServiceV4 userService;

    @Async
    public synchronized void dealCourseExercisesStatistics(PracticeCard answerCard) throws BizException{
        try{
            PracticeForCoursePaper practiceForCoursePaper = (PracticeForCoursePaper)answerCard.getPaper();
            String existsKey = CourseCacheKey.getCourseWorkDealData(practiceForCoursePaper.getCourseType(), practiceForCoursePaper.getCourseId());
            String rankInfoKey = CourseCacheKey.getCourseWorkRankInfo(practiceForCoursePaper.getCourseType(), practiceForCoursePaper.getCourseId());
            HashOperations<String, String, String> existsHash = redisTemplate.opsForHash();
            ZSetOperations<String, String> rankInfoZset = redisTemplate.opsForZSet();

            /**
             * 如果用户已经提交过不处理
             */
            if(existsHash.hasKey(existsKey, String.valueOf(answerCard.getUserId()))){
                return;
            }
            Example example = new Example(CourseExercisesStatistics.class);
            example.and()
                    .andEqualTo("courseId", practiceForCoursePaper.getCourseId())
                    .andEqualTo("courseType", practiceForCoursePaper.getType())
                    .andEqualTo("status", YesOrNoStatus.YES.getCode());
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

            int times = Arrays.stream(answerCard.getTimes()).sum();
            long score = (practiceForCoursePaper.getQcount() - answerCard.getRcount()) * 100000 + times;

            UserRankInfo userRankInfo = UserRankInfo.builder()
                    .uid(answerCard.getUserId())
                    .rcount(answerCard.getRcount())
                    .expendTime(times)
                    .submitTimeInfo(System.currentTimeMillis())
                    .build();

            rankInfoZset.add(rankInfoKey, String.valueOf(answerCard.getUserId()), score);
            existsHash.put(existsKey, String.valueOf(answerCard.getUserId()), JSONObject.toJSONString(userRankInfo));
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
        rankInfo.put("avgTimeCost", 0);
        rankInfo.put("avgCorrect", 0);
        rankInfo.put("maxCorrect", 0);
        rankInfo.put("ranks", 0);
        rankInfo.put("myRank", Lists.newArrayList());

        try {
            PracticeForCoursePaper practiceForCoursePaper = (PracticeForCoursePaper) practiceCard.getPaper();
            Example example = new Example(CourseExercisesStatistics.class);
            example.and()
                    .andEqualTo("courseId", practiceForCoursePaper.getCourseId())
                    .andEqualTo("courseType", practiceForCoursePaper.getCourseType())
                    .andEqualTo("status", YesOrNoStatus.YES.getCode());

            CourseExercisesStatistics courseExercisesStatistics = courseExercisesStatisticsMapper.selectOneByExample(example);
            if(null == courseExercisesStatistics){
                return rankInfo;
            }
            rankInfo.put("avgTimeCost", courseExercisesStatistics.getCosts() / courseExercisesStatistics.getCounts());
            rankInfo.put("avgCorrect", courseExercisesStatistics.getCorrects() / courseExercisesStatistics.getCounts());
            String rankKey = CourseCacheKey.getCourseWorkRankInfo(practiceForCoursePaper.getCourseType(), practiceForCoursePaper.getCourseId());
            ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
            HashOperations<String, String, String> existHash = redisTemplate.opsForHash();
            String existsKey = CourseCacheKey.getCourseWorkDealData(practiceForCoursePaper.getCourseType(), practiceForCoursePaper.getCourseId());
            long myRank = zSetOperations.rank(rankKey, String.valueOf(practiceCard.getUserId())) + 1;
            Set<String> userIdRanks = zSetOperations.range(rankKey, 0, 9);
            List<UserRankInfo> userRankInfoArrayList = Lists.newArrayList();
            userRankInfoArrayList.addAll(userIdRanks.stream().map(item -> {
                String value = existHash.get(existsKey, item);
                UserRankInfo userRankInfo = JSONObject.parseObject(value, UserRankInfo.class);
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
        }catch (Exception e){
            log.error("obtainCourseRankInfo caught an error!{}", e);
            return rankInfo;
        }
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
        AtomicInteger rank = new AtomicInteger(1);
        userRankInfoArrayList.forEach(userRankInfo ->{
            Map<String,Object> detail = userInfoMaps.get(String.valueOf(userRankInfo.getUid()));
            userRankInfo.setAvatar(String.valueOf(detail.get("avatar")));
            userRankInfo.setUname(String.valueOf(detail.get("nick")));
            userRankInfo.setRank(rank.getAndIncrement());
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
