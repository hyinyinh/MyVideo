package com.hy.tiktok.controller;

import com.hy.tiktok.bo.CommentBO;
import com.hy.tiktok.enums.YesOrNo;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.service.CommentService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.utils.PageSetUtil;
import com.hy.tiktok.vo.CommentVO;
import com.rabbitmq.client.AMQP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/17 19:29
 */
@RestController
@CrossOrigin
@Api(tags = "CommentController 评论功能接口")
@RequestMapping("comment")
public class CommentController extends BaseInfoProperties {
    @Autowired
    private CommentService commentService;

    @PostMapping("create")
    public GraceJSONResult create(@RequestBody @Valid CommentBO commentBO){
        CommentVO commentVO = commentService.CreateComment(commentBO);
        return GraceJSONResult.ok(commentVO);
    }

    @GetMapping("counts")
    public GraceJSONResult count(@RequestParam String vlogId){
        String countStr = redisOperator.get(REDIS_VLOG_COMMENT_COUNTS+":"+vlogId);
        if(StringUtils.isBlank(countStr)){
            countStr = "0";
        }
        return GraceJSONResult.ok(Integer.valueOf(countStr));
    }

    @GetMapping("list")
    public GraceJSONResult list(@RequestParam String vlogId,
                                @RequestParam(defaultValue = "") String userId,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize){
        PageSetUtil.setPage(page,pageSize);
        return GraceJSONResult.ok(commentService.getCommentList(vlogId, userId,page, pageSize));
    }

    @DeleteMapping("delete")
    public GraceJSONResult delete(@RequestParam String commentUserId,
                                  @RequestParam String commentId,
                                  @RequestParam String vlogId){
        commentService.deleteComment(commentUserId,commentId,vlogId);
        return GraceJSONResult.ok();
    }

    @PostMapping("like")
    public GraceJSONResult like(@RequestParam String commentId,
                                @RequestParam String userId){
        commentService.like(commentId,userId);
        return GraceJSONResult.ok();
    }

    @PostMapping("unlike")
    public GraceJSONResult unlike(@RequestParam String commentId,
                                    @RequestParam String userId){
        commentService.unlike(commentId,userId);
        return GraceJSONResult.ok();
    }


}
