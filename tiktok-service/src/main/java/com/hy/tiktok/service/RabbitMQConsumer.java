package com.hy.tiktok.service;

import com.hy.tiktok.exceptions.GraceException;
import com.hy.tiktok.grace.ResponseStatusEnum;
import com.hy.tiktok.mo.MessageMO;
import com.hy.tiktok.service.common.RabbitMQConfig;
import com.hy.tiktok.utils.JsonUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/28 13:40
 */

@Component
public class RabbitMQConsumer {
    @Autowired
    private MsgService msgService;

    @RabbitListener(queues = RabbitMQConfig.MSG_SEND_QUEUE)
    public void receiveMsg(String payload,Message message){
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        MessageMO messageMO = JsonUtils.jsonToPojo(payload,MessageMO.class);

        Integer msgType = messageMO.getMsgType();


        if(msgType >= 1 && msgType <=5){
            if(routingKey.equalsIgnoreCase("msg.send."+ msgType)){
                msgService.createMsg(messageMO.getFromUserId(),
                                    messageMO.getToUserId(),
                                    msgType,
                                    messageMO.getMsgContent());
            }else {
                GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
            }
        }else if(msgType >= 6 && msgType <=10){
            if(routingKey.equalsIgnoreCase("msg.send."+ msgType)){
                msgService.delMsg(messageMO.getFromUserId(),
                                     messageMO.getToUserId(),
                                     msgType-5,
                                     messageMO.getMsgContent());
            }else {
                GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
            }
        }
    }
}
