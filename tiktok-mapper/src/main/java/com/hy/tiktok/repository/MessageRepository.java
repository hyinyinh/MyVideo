package com.hy.tiktok.repository;

import com.hy.tiktok.mo.MessageMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/26 15:40
 */
@Repository
public interface MessageRepository extends MongoRepository<MessageMO,String> {
    public List<MessageMO> findAllByToUserIdEqualsOrderByCreateTimeDesc(String toUserId, Pageable pageable);
    public MessageMO deleteMessageMOByFromUserIdAndToUserIdAndMsgTypeAndMsgContent(String fromUserId,
                                                                                   String toUserId,
                                                                                   Integer msgType,
                                                                                   Map<String,Object> msgContent);
}
