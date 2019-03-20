package com.huatu.tiku.course.service.exam.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.common.ArticleTypeListEnum;
import com.huatu.tiku.course.netschool.api.ExamNetSchoolService;
import com.huatu.tiku.course.service.exam.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @创建人 lizhenjuan
 * @创建时间 2019/2/19
 * @描述 备考精华相关
 */

@Slf4j
@Service
public class ExamServiceImpl implements ExamService {


    @Autowired
    private ExamNetSchoolService examNetSchoolService;

    @Autowired
    RedisTemplate redisTemplate;


    /**
     * 备考精华列表查询
     *
     * @param
     * @return
     */
    public Object getArticleList(int type, int page, int pageSize, int subject) {
        HashMap param = Maps.newHashMap();
        param.put("type", type);
        param.put("page", page);
        param.put("pageSize", pageSize);

        List<HashMap> articleList = new ArrayList<>();
        String articleListKey = articleListKey(type, page, subject);
        log.info("articleListKey是:{},科目是:{}", articleListKey, subject);
        HashOperations hashOperations = redisTemplate.opsForHash();
        Object article = hashOperations.get(articleListKey, subject + "");
        if (null != article) {
            articleList = getArticleInfo(article, false);
        } else {
            //走redis
            NetSchoolResponse articleResultList = examNetSchoolService.getArticleList(param);
            if (articleResultList.getData() != null) {
                hashOperations.put(articleListKey, subject + "", articleResultList.getData());
                redisTemplate.expire(articleListKey, 5, TimeUnit.MINUTES);
                final Object data = articleResultList.getData();
                getArticleInfo(data, true);
                articleList = getArticleInfo(data, false);
            }
        }

        return articleList;
    }

    /**
     *
     */
    public List<HashMap> getArticleInfo(Object article, Boolean flag) {
        List<HashMap> articleList = new ArrayList<>();
        HashMap map = JSON.parseObject(JSON.toJSON(article).toString(), HashMap.class);

        if (null != map) {
            Object data = map.get("data");
            articleList = JSON.parseArray(data.toString(), HashMap.class);
        }
        HashOperations hashOperations = redisTemplate.opsForHash();

        List<HashMap> collect = articleList.stream().map(articleInfo -> {
            Object articleId = articleInfo.get("id");
            if (null != articleId) {
                if (flag) {
                    hashOperations.put(buildArticleLikeCount(articleId.toString()), articleId.toString(), articleInfo.get("goodPost"));
                } else {
                    //去redis中获取
                    Object likeCount = hashOperations.get(buildArticleLikeCount(articleId.toString()), articleId.toString());
                    articleInfo.put("goodPost", likeCount);
                }
            }
            return articleInfo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 备考精华文章详情
     *
     * @param id
     * @return
     */
    public Object detail(int id) {
        HashMap params = new HashMap();
        params.put("id", id);
        HashOperations hashOperations = redisTemplate.opsForHash();
        String articleDetailKey = articleDetail(id);
        log.info("articleDetailKey是:{}", articleDetailKey);
        Object articleDetail = hashOperations.get(articleDetailKey, String.valueOf(id));
        if (null != articleDetail) {
            return articleDetail;
        }
        NetSchoolResponse detailResponse = examNetSchoolService.detail(params);
        if (detailResponse.getData() != null) {
            hashOperations.put(articleDetailKey, String.valueOf(id), detailResponse.getData());
            redisTemplate.expire(articleDetailKey, 5, TimeUnit.MINUTES);
        }
        return detailResponse.getData();
    }

    /**
     * 备考精华分类列表（直接写死在Java）
     *
     * @return
     */
    public List<HashMap<String, Object>> typeList() {
        ArticleTypeListEnum[] values = ArticleTypeListEnum.values();
        List<HashMap<String, Object>> result = new ArrayList<>();
        for (ArticleTypeListEnum article : values) {
            HashMap map = new HashMap();
            map.put("sort", article.getSort());
            map.put("type", article.getCode());
            map.put("name", article.getName());
            result.add(map);
        }
        return result;
    }


    /**
     * 用户点赞
     *
     * @param map
     * @return
     */
    public NetSchoolResponse like(HashMap map) {
        NetSchoolResponse like = examNetSchoolService.like(map);
        //清除文章详情缓存
        int id = (int) map.get("id");
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(articleDetail(id), String.valueOf(id));
        //文章点赞
        String articleLikeCountKey = buildArticleLikeCount(String.valueOf(id));
        if (map.get("type").equals(1)) {
            //点赞
            hashOperations.increment(articleLikeCountKey, String.valueOf(id), 1);
        } else {
            //取消点赞
            hashOperations.increment(articleLikeCountKey, String.valueOf(id), -1);
        }
        return like;
    }


    /**
     * 备考精华列表缓存key
     */
    public String articleListKey(int type, int page, int subject) {
        StringBuffer listKey = new StringBuffer();
        return listKey.append("article:cache").append(":")
                .append(subject).append(subject)
                .append(page).append(":")
                .append(type).toString();
    }

    /**
     * 文章详情缓存key
     */
    public String articleDetail(int aid) {
        StrBuilder detailKey = new StrBuilder();
        return detailKey.append("article").append(":")
                .append("detail").append(":")
                .append(aid).toString();
    }

    /**
     * 科目点赞数量
     *
     * @return
     */
    public String buildArticleLikeCount(String aid) {
        StringBuffer articleLike = new StringBuffer();
        return articleLike.append("article:like:count").append(":")
                .append(aid)
                .toString();
    }


}

