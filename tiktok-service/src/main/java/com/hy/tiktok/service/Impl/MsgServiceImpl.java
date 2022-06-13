package com.hy.tiktok.service.Impl;

import com.hy.tiktok.enums.MessageEnum;
import com.hy.tiktok.mo.MessageMO;
import com.hy.tiktok.pojo.Users;
import com.hy.tiktok.repository.MessageRepository;
import com.hy.tiktok.service.MsgService;
import com.hy.tiktok.service.UserService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/26 15:37
 */
@Service
public class MsgServiceImpl extends BaseInfoProperties implements MsgService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserService userService;

    @Override
    public void createMsg(String fromUserId,
                          String toUserId,
                          Integer msgType,
                          Map msgContent) {
        MessageMO messageMO = new MessageMO();
        Users fromUser = userService.getUser(fromUserId);

        messageMO.setFromUserId(fromUserId);
        messageMO.setFromNickname(fromUser.getNickname());
        messageMO.setFromFace(fromUser.getFace());


        messageMO.setToUserId(toUserId);

        messageMO.setMsgType(msgType);
        if(msgContent != null){
            messageMO.setMsgContent(msgContent);
        }

        messageMO.setCreateTime(new Date());

        messageRepository.save(messageMO);
    }

    @Override
    public List<MessageMO> getlist(String toUserId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of( page,
                                            pageSize,
                                            Sort.Direction.DESC,
                                    "createTime");

        List<MessageMO> messagelist = messageRepository
                .findAllByToUserIdEqualsOrderByCreateTimeDesc(toUserId, pageable);
        for (MessageMO message : messagelist) {
            if(message.getMsgType()!=null && message.getMsgType() .equals(MessageEnum.FOLLOW_YOU)){
                Map map = message.getMsgContent();
                if(map == null){
                    map = new HashMap();
                }
                String isFriendStr = redisOperator.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP
                                                    + ":" + message.getToUserId()
                                                    + ":" + message.getFromUserId());
                if(isFriendStr !=null && isFriendStr.equalsIgnoreCase("1")){
                    map.put("isFriend",true);
                }else{
                    map.put("isFriend",false);
                }
                message.setMsgContent(map);
            }
        }
        return messagelist;
    }

    @Override
    public void delMsg(String fromUserId, String toUserId, Integer msgType, Map msgContent) {
        messageRepository.deleteMessageMOByFromUserIdAndToUserIdAndMsgTypeAndMsgContent(fromUserId,
                                                                                        toUserId,
                                                                                        msgType,
                                                                                        msgContent);
    }

}
