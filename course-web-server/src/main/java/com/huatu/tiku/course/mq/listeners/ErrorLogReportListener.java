package com.huatu.tiku.course.mq.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.chatbot.DingtalkChatbotClient;
import com.dingtalk.chatbot.message.TextMessage;
import com.huatu.tiku.course.consts.RabbitMqConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 上报错误消息处理
 * 
 * @author zhangchong
 *
 */
@Slf4j
@Component
public class ErrorLogReportListener {
	
	@Value("${dingding.webhook:https://oapi.dingtalk.com/robot/send?access_token=338405433f6b9a54abf4d1e1681857c005a304a3ddbe19b38ae4ea869bd2adbd}")
	private String webhook;
	
	@RabbitListener(queues=RabbitMqConstants.DATA_REPORT)
	public void onMessage(String message) {
		log.error("error log:{}", message);
		JSONObject json = JSONObject.parseObject(message);
		if(json != null) {
			String stacktrace = json.getString("stacktrace");
			sendMsg(stacktrace);
		}
		
	}
	
	private void sendMsg(String content){
		try {
			DingtalkChatbotClient client = new DingtalkChatbotClient();
			TextMessage msg = new TextMessage(content);
			msg.setIsAtAll(true);
			client.send(webhook, msg);
		} catch (Exception e) {
			log.error("dingding sendMsg error:{}",e);
		}
	}

}
