package com.hy.tiktok.service;

import com.hy.tiktok.mo.MessageMO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/26 15:36
 */
public interface MsgService {
    public void createMsg(String fromUserId, String toUserId, Integer msgType, Map msgContent);
    public List<MessageMO> getlist(String toUserId,Integer page,Integer pageSize);
    public void delMsg(String fromUserId, String toUserId, Integer msgType, Map msgContent);
}
