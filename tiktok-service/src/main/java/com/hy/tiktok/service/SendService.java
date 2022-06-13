package com.hy.tiktok.service;

import com.hy.tiktok.enums.MessageEnum;
import com.hy.tiktok.mo.MessageMO;
import com.hy.tiktok.service.common.RabbitMQConfig;
import com.hy.tiktok.utils.JsonUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/28 19:15
 */
@Service
public class SendService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void sendMessage(String toUserId, String userId, Map<String, Object> map, Integer type) {
        MessageMO msg = new MessageMO();
        msg.setFromUserId(userId);
        msg.setToUserId(toUserId);
        msg.setMsgType(type);
        msg.setMsgContent(map);

        rabbitTemplate.convertAndSend(RabbitMQConfig.MSG_SEND_EXCHANGE,
                "msg.send." + type,
                JsonUtils.objectToJson(msg));
    }
}
