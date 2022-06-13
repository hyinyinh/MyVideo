package com.hy.tiktok.service.Impl;

import com.github.pagehelper.PageHelper;
import com.hy.tiktok.bo.CommentBO;
import com.hy.tiktok.enums.MessageEnum;
import com.hy.tiktok.enums.YesOrNo;
import com.hy.tiktok.mapper.CommentMapper;
import com.hy.tiktok.mapper.CommentMapperCustom;
import com.hy.tiktok.mo.MessageMO;
import com.hy.tiktok.pojo.Comment;
import com.hy.tiktok.pojo.Users;
import com.hy.tiktok.pojo.Vlog;
import com.hy.tiktok.service.*;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.service.common.RabbitMQConfig;
import com.hy.tiktok.utils.JsonUtils;
import com.hy.tiktok.utils.PagedGridResult;
import com.hy.tiktok.utils.RedisOperator;
import com.hy.tiktok.vo.CommentVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/24 15:43
 */
@Service
public class CommentServiceImpl extends BaseInfoProperties implements CommentService {
    @Autowired
    private Sid sid;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentMapperCustom commentMapperCustom;
    @Autowired
    private VlogService vlogService;
    @Autowired
    private UserService userService;
    @Autowired
    private SendService sendService;

    @Override
    public CommentVO CreateComment(CommentBO commentBO) {
        String cid = sid.nextShort();
        Comment comment = new Comment();
        comment.setId(cid);
        comment.setVlogerId(commentBO.getVlogerId());
        comment.setVlogId(commentBO.getVlogId());

        comment.setFatherCommentId(commentBO.getFatherCommentId());
        comment.setContent(commentBO.getContent());
        comment.setCommentUserId(commentBO.getCommentUserId());

        comment.setCreateTime(new Date());
        comment.setLikeCounts(0);

        commentMapper.insert(comment);
        //评论总数的累加
        redisOperator.increment(REDIS_VLOG_COMMENT_COUNTS+":"+ commentBO.getVlogId(),1);

        //留言后的最新评论返回给前端
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(commentBO,commentVO);

        //默认是评论视频
        Integer type = MessageEnum.COMMENT_VLOG.type;
        //如果存在父评论id 则说明是评论
        if(StringUtils.isNotBlank(commentBO.getFatherCommentId()) &&
                !commentBO.getFatherCommentId().equalsIgnoreCase("0")){
            type = MessageEnum.REPLY_YOU.type;
        }

        Map<String,Object> map = new HashMap<>();
        map.put("commentId", comment.getId());
        map.put("commentContent",commentBO.getContent());
        Vlog vlog = vlogService.getVlogById(commentBO.getVlogId());
        map.put("vlogId",vlog.getId());
        map.put("vlogCover",vlog.getCover());

        if(type == MessageEnum.REPLY_YOU.type){
            Users fatherCommentUser = userService.getUser(commentBO.getFatherCommentId());
            sendService.sendMessage(fatherCommentUser.getId(), commentBO.getCommentUserId(), map, type);
        }else{
            sendService.sendMessage(commentBO.getVlogerId(), commentBO.getCommentUserId(), map, type);
        }

        return commentVO;
    }

    @Override
    public void deleteComment(String commentUserId,String commentId,String vlogId) {
        Comment comment = getCommentById(commentId);

        Integer type = MessageEnum.UNCOMMENT_VLOG.type;

        String fatherCommentId = comment.getFatherCommentId();
        if(StringUtils.isNotBlank(fatherCommentId) && !fatherCommentId.equalsIgnoreCase("0")){
            type = MessageEnum.UNREPLY_YOU.type;
        }

        Map<String,Object> map = new HashMap<>();
        map.put("commentId", commentId);
        Vlog vlog = vlogService.getVlogById(vlogId);
        map.put("vlogId",vlogId);
        map.put("vlogCover",vlog.getCover());
        map.put("commentContent",comment.getContent());

        sendService.sendMessage(vlog.getVlogerId(), commentUserId, map, type);

        commentMapper.delete(comment);
    }

    @Override
    public void like(String commentId, String userId) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);

        Map<String,Object> map = new HashMap<>();
        map.put("commentId", comment.getId());

        String vlogId = comment.getVlogId();
        Vlog vlog = vlogService.getVlogById(vlogId);
        map.put("vlogId",vlog.getId());
        map.put("vlogCover",vlog.getCover());

        sendService.sendMessage(vlog.getVlogerId(), userId, map, MessageEnum.LIKE_COMMENT.type);

        redisOperator.hset(REDIS_USER_LIKE_COMMENT,commentId+":"+userId,"1");
        redisOperator.incrementHash(REDIS_VLOG_COMMENT_LIKED_COUNTS,commentId,1);
    }

    @Override
    public void unlike(String commentId, String userId) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);

        Map<String,Object> map = new HashMap<>();
        map.put("commentId", comment.getId());

        String vlogId = comment.getVlogId();
        Vlog vlog = vlogService.getVlogById(vlogId);
        map.put("vlogId",vlog.getId());
        map.put("vlogCover",vlog.getCover());

        sendService.sendMessage(vlog.getVlogerId(), userId, map, MessageEnum.UNLIKE_COMMENT.type);

        redisOperator.hdel(REDIS_USER_LIKE_COMMENT,commentId+":"+userId, "1");
        redisOperator.decrementHash(REDIS_VLOG_COMMENT_LIKED_COUNTS,commentId,1);
    }


    //FIXME 没有将点赞数放入数据库中导致评论不按照降序排列
    @Override
    public PagedGridResult getCommentList(String vlogId, String userId,Integer page, Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("vlogId",vlogId);
        List<CommentVO> commentList = commentMapperCustom.getCommentList(map);
        for (CommentVO comment : commentList) {
            int likeCount = 0;
            String likeCountStr = redisOperator.hget(REDIS_VLOG_COMMENT_LIKED_COUNTS, comment.getCommentId());
            if(StringUtils.isNotBlank(likeCountStr)){
                likeCount = Integer.valueOf(likeCountStr);
            }
            comment.setLikeCounts(likeCount);

            String isLike = redisOperator.hget(REDIS_USER_LIKE_COMMENT, comment.getCommentId() + ":" + userId);
            if (StringUtils.isNotBlank(isLike) && isLike.equals("1")){
                comment.setIsLike(YesOrNo.YES.type);
            }
        }
        PageHelper.startPage(page,pageSize);
        return setterPagedGrid(commentList,page);
    }

    public Comment getCommentById(String commentId){
        return commentMapper.selectByPrimaryKey(commentId);
    }


}
