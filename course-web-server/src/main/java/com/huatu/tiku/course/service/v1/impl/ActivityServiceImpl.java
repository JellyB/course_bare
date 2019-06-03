package com.huatu.tiku.course.service.v1.impl;

import java.time.LocalDate;
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
			JSONObject configObject = configObjectCache.get("618");
			String currentKey = LocalDate.now().toString();
			Integer coin = (Integer) configObject.get(currentKey);
			if (coin != null) {
				final SetOperations<String, String> setOperations = redisTemplate.opsForSet();
				if (!setOperations.isMember(currentKey, uname)) {
					RewardMessage msg = RewardMessage.builder().gold(coin)
							.action(RewardAction.ActionType.ACTIVTY.name()).experience(1).bizId(currentKey).uname(uname)
							.timestamp(System.currentTimeMillis()).build();
					log.info("618活动用户uname:{}赠送图币{},bizId为:{}", uname, coin, currentKey);
					rabbitTemplate.convertAndSend("", RabbitConsts.QUEUE_REWARD_ACTION, msg);
					setOperations.add(currentKey, uname);
					redisTemplate.expire(currentKey, 7, TimeUnit.DAYS);
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

}
