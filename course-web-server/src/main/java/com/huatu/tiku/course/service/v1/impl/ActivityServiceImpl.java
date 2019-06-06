package com.huatu.tiku.course.service.v1.impl;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.huatu.tiku.common.bean.reward.RewardMessage;
import com.huatu.tiku.common.consts.RabbitConsts;
import com.huatu.tiku.course.common.ActivityStatusEnum;
import com.huatu.tiku.course.service.v1.ActivityService;
import com.huatu.tiku.springboot.basic.reward.RewardAction;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author zhangchong
 *
 */
@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {

	@Autowired
	private RedisTemplate redisTemplate;

	@Value("${activity.sign.conf}")
	private String activityConfig;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private String CACHENAME = "618";

	private String CACHEPREFIX = "activity_618_";

	private LoadingCache<Object, JSONObject> configObjectCache = CacheBuilder.newBuilder().maximumSize(10)
			.expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<Object, JSONObject>() {
				@Override
				public JSONObject load(Object key) throws Exception {
					return JSONObject.parseObject(activityConfig);
				}
			});

	@Override
	public int signGiveCoin(String uname) {
		try {
			JSONObject configObject = configObjectCache.get(CACHENAME);
			String currentKey = LocalDate.now().toString();
			Integer coin = (Integer) configObject.get(currentKey);
			if (coin != null) {
				String activityKey = CACHEPREFIX + currentKey;
				final SetOperations<String, String> setOperations = redisTemplate.opsForSet();
				if (!setOperations.isMember(activityKey, uname)) {
					RewardMessage msg = RewardMessage.builder().gold(coin)
							.action(RewardAction.ActionType.ACTIVTY.name()).experience(1).bizId(currentKey).uname(uname)
							.timestamp(System.currentTimeMillis()).build();
					log.info("618活动用户uname:{}赠送图币{},bizId为:{}", uname, coin, currentKey);
					rabbitTemplate.convertAndSend("", RabbitConsts.QUEUE_REWARD_ACTION, msg);
					setOperations.add(activityKey, uname);
					redisTemplate.expire(activityKey, 7, TimeUnit.DAYS);
					return ActivityStatusEnum.SUCCESS.getCode();
				} else {
					log.info("618活动用户uname:{}已经赠送过图币{},bizId为:{}", uname, coin, currentKey);
					return ActivityStatusEnum.SIGNED.getCode();

				}
			}
			return ActivityStatusEnum.END.getCode();
		} catch (Exception e) {
			log.error("signGiveCoin error:{}", e);
		}
		return ActivityStatusEnum.ERROR.getCode();
	}

	/**
	 * 签到记录
	 */
	@Override
	public Object signList(String uname) {
		try {
			final SetOperations<String, String> setOperations = redisTemplate.opsForSet();
			JSONObject configObject = configObjectCache.get(CACHENAME);
			Set<String> it = configObject.keySet();
			Map<String, Integer> signMap = Maps.newHashMap();
			it.forEach(key -> {
				String activityKey = CACHEPREFIX + key;
				LocalDate activityDate = LocalDate.parse(key);
				if (setOperations.isMember(activityKey, uname)) {
					// 已经签到
					signMap.put(key, ActivityStatusEnum.SIGNED.getCode());
				} else if (LocalDate.now().compareTo(activityDate) > 0) {
					// 已过期
					signMap.put(key, ActivityStatusEnum.END.getCode());
				} else {
					// 未签到
					signMap.put(key, ActivityStatusEnum.UNDO.getCode());
				}
			});
			return signMap;
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return Maps.newHashMap();
	}

}
