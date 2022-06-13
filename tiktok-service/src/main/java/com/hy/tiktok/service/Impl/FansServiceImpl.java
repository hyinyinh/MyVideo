package com.hy.tiktok.service.Impl;

import com.github.pagehelper.PageHelper;
import com.hy.tiktok.enums.MessageEnum;
import com.hy.tiktok.enums.YesOrNo;
import com.hy.tiktok.mapper.FansMapper;
import com.hy.tiktok.mapper.FansMapperCustom;
import com.hy.tiktok.mo.MessageMO;
import com.hy.tiktok.pojo.Fans;
import com.hy.tiktok.service.FansService;
import com.hy.tiktok.service.MsgService;
import com.hy.tiktok.service.SendService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.service.common.RabbitMQConfig;
import com.hy.tiktok.utils.JsonUtils;
import com.hy.tiktok.utils.PagedGridResult;
import com.hy.tiktok.vo.FansVO;
import com.hy.tiktok.vo.VlogerVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/23 17:24
 */
@Service
public class FansServiceImpl extends BaseInfoProperties implements FansService {
    @Autowired
    private FansMapper fansMapper;
    @Autowired
    private FansMapperCustom fansMapperCustom;
    @Autowired
    private Sid sid;
    @Autowired
    private MsgService msgService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SendService sendService;

    @Override
    @Transactional
    public void doFollow(String myId, String vlogerId) {
        String fid = sid.nextShort();
        Fans fans = new Fans();
        fans.setId(fid);
        fans.setFanId(myId);
        fans.setVlogerId(vlogerId);
        //查对方有没有关注我
        Fans vloger = queryFanRelationShip(vlogerId, myId);
        if(vloger!=null){
            fans.setIsFanFriendOfMine(YesOrNo.YES.type);
            vloger.setIsFanFriendOfMine(YesOrNo.YES.type);
            fansMapper.updateByPrimaryKeySelective(vloger);
        }else{
            fans.setIsFanFriendOfMine(YesOrNo.NO.type);
        }
        fansMapper.insert(fans);
        //msgService.createMsg(myId,vlogerId, MessageEnum.FOLLOW_YOU.type,null);

        sendService.sendMessage(vlogerId,myId,null,MessageEnum.FOLLOW_YOU.type);

        //博主的粉丝数+1 我的关注数+1
        redisOperator.increment(REDIS_MY_FOLLOWS_COUNTS+":"+myId,1);
        redisOperator.increment(REDIS_MY_FANS_COUNTS+":"+vlogerId,1);
        //我和博主的关联关系
        redisOperator.set(REDIS_FANS_AND_VLOGGER_RELATIONSHIP+":"+myId+":"+vlogerId,"1");
    }

    @Override
    @Transactional
    public void doCancel(String myId, String vlogerId) {
        //先判断是否为朋友关系 如果是朋友关系 需要更改
        //查自己
        Fans fan = queryFanRelationShip(myId, vlogerId);
        if(fan!=null && fan.getIsFanFriendOfMine() == YesOrNo.YES.type){
            //查对方 改对方的状态
            Fans vloger = queryFanRelationShip(vlogerId, myId);
            vloger.setIsFanFriendOfMine(YesOrNo.NO.type);
            fansMapper.updateByPrimaryKeySelective(vloger);
        }
        fansMapper.delete(fan);

        sendService.sendMessage(vlogerId,myId,null,MessageEnum.UNFOLLOW_YOU.type);

        //博主的粉丝数+1 我的关注数+1
        redisOperator.decrement(REDIS_MY_FOLLOWS_COUNTS+":"+myId,1);
        redisOperator.decrement(REDIS_MY_FANS_COUNTS+":"+vlogerId,1);
        //我和博主的关联关系
        redisOperator.del(REDIS_FANS_AND_VLOGGER_RELATIONSHIP+":"+myId+":"+vlogerId);
    }

    @Override
    public boolean queryDoIFollowVloger(String myId, String vlogerId) {
        String follow = redisOperator.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId);
        if(StringUtils.isNotBlank(follow) && follow.equals("1")){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public PagedGridResult queryMyFollows(String myId, Integer page, Integer pageSize) {
        Map<String,Object> map = new HashMap<>();
        map.put("myId",myId);
        List<VlogerVO> vlogerVOS = fansMapperCustom.queryMyFollows(map);
        PageHelper.startPage(page,pageSize);
        return setterPagedGrid(vlogerVOS,page);
    }

    @Override
    public PagedGridResult queryMyFans(String myId, Integer page, Integer pageSize) {
        Map<String,Object> map = new HashMap<>();
        map.put("myId",myId);
        //查询我又没有关注对方 实现回粉功能
        List<FansVO> fansVOS = fansMapperCustom.queryMyFans(map);
        for(FansVO fan :fansVOS){
            String relation = redisOperator.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + fan.getFanId());
            if(StringUtils.isNotBlank(relation) && relation.equals("1")){
                fan.setFriend(true);
            }
        }
        PageHelper.startPage(page,pageSize);
        return setterPagedGrid(fansVOS,page);
    }


    /**
    * @Description: 查询数据库 看对方是否关注我 确定朋友关系
    */
    public Fans queryFanRelationShip(String fanId,String vlogerId){
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("fanId",fanId);
        criteria.andEqualTo("vlogerId",vlogerId);
        List fans = fansMapper.selectByExample(example);
        Fans fan = null;
        if(fans!=null && fans.size()>0){
            fan = (Fans) fans.get(0);
        }
        return fan;
    }

}
