package com.hy.tiktok.controller;

import com.hy.tiktok.bo.CommentBO;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.mo.MessageMO;
import com.hy.tiktok.service.CommentService;
import com.hy.tiktok.service.MsgService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.vo.CommentVO;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/17 19:29
 */
@RestController
@CrossOrigin
@Api(tags = "MessageController 消息功能接口")
@RequestMapping("msg")
public class MessageController extends BaseInfoProperties {
    @Autowired
    private MsgService msgService;

    @GetMapping("list")
    public GraceJSONResult list(@RequestParam String userId,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize){
        //mongodb从0分页
        if(page == null){
            page = COMMON_START_PAGE_ZERO;
        }
        if(pageSize == null){
            pageSize = COMMON_PAGE_SIZE;
        }
        List<MessageMO> list = msgService.getlist(userId, page, pageSize);
        return GraceJSONResult.ok(list);
    }

}
