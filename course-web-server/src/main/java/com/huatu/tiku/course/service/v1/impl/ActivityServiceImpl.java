package com.huatu.tiku.course.service.v1.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.huatu.tiku.course.consts.UserInfo;
import com.huatu.tiku.course.service.v6.SensorsService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
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

	@Autowired
	private SensorsService sensorsService;

	private String CACHENAME = "618";

	private String CACHEPREFIX = "activity_618_";

	private String CACHE_PREFIX_EXIST = "activity_618_exist";

	private String CACHE_PREFIX_HASH_KEY = "activity_618_hash_key";

	private LoadingCache<Object, JSONObject> configObjectCache = CacheBuilder.newBuilder().maximumSize(10)
			.expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<Object, JSONObject>() {
				@Override
				public JSONObject load(Object key) throws Exception {
					return JSONObject.parseObject(activityConfig);
				}
			});

	@Override
	public int signGiveCoin(String userName, String ucId) {
		try {
			JSONObject configObject = configObjectCache.get(CACHENAME);
			String currentKey = LocalDate.now().toString();
			Integer coin = (Integer) configObject.get(currentKey);
			if (coin != null) {
				String activityKey = CACHEPREFIX + currentKey;
				final SetOperations<String, String> setOperations = redisTemplate.opsForSet();
				final HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
				if (!setOperations.isMember(activityKey, userName)) {
					RewardMessage msg = RewardMessage.builder().gold(coin)
							.action(RewardAction.ActionType.ACTIVTY.name()).experience(1).bizId(currentKey).uname(userName)
							.timestamp(System.currentTimeMillis()).build();
					rabbitTemplate.convertAndSend("", RabbitConsts.QUEUE_REWARD_ACTION, msg);
					setOperations.add(activityKey, userName);
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					UserInfo userInfo = new UserInfo(userName, ucId, simpleDateFormat.format(new Date()), coin.intValue());
					hashOperations.put(CACHE_PREFIX_HASH_KEY, currentKey, JSONObject.toJSONString(userInfo));
					redisTemplate.expire(activityKey, 7, TimeUnit.DAYS);
					log.info("618活动用户 userName:{}赠送图币{},bizId为:{},ucId:{}", userName, coin, currentKey, ucId);
					return ActivityStatusEnum.SUCCESS.getCode();
				} else {
					log.info("618活动用户uname:{}已经赠送过图币{},bizId为:{}", userName, coin, currentKey);
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

	/**
	 * 每天的零晨 2 点执行定时任务
	 */
	//@Scheduled(cron = "0 0 2 * * ?")
	public void dealUserDataByDay(){
		LocalDate localDate = LocalDate.now().minusDays(1);
		String currentKey = localDate.toString();
		log.info("开始处理当前时间任务:{}", currentKey);
		final SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		if(setOperations.isMember(CACHE_PREFIX_EXIST, currentKey)){
			log.info("当前 currentKey:{}已经被其他机器处理过!", currentKey);
		}else{
			setOperations.add(CACHE_PREFIX_EXIST, currentKey);
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.execute(() -> dealCurrentKey(currentKey));
			executorService.shutdown();
		}
	}

	/**
	 *
	 * @param currentKey
	 */
	private void dealCurrentKey(String currentKey){
		String activityKey = CACHEPREFIX + currentKey;
		final SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		final HashOperations<String,String, String> hashOperations = redisTemplate.opsForHash();
		Set<String> strings = setOperations.members(activityKey);
		List<String> result = hashOperations.multiGet(CACHE_PREFIX_HASH_KEY, strings);
		for(String str : result){
			UserInfo userInfo = JSONObject.parseObject(str, UserInfo.class);
			sensorsService.reportActivitySign(userInfo);
		}
	}
}
