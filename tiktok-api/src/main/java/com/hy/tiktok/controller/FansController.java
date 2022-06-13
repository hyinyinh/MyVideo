package com.hy.tiktok.controller;

import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.grace.ResponseStatusEnum;
import com.hy.tiktok.service.FansService;
import com.hy.tiktok.service.UserService;
import com.hy.tiktok.service.VlogService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.utils.PageSetUtil;
import com.hy.tiktok.utils.PagedGridResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/23 18:04
 */
@RestController
@Api("FansController 粉丝业务的接口")
@Slf4j
@CrossOrigin
@RequestMapping("fans")
public class FansController extends BaseInfoProperties {
    @Autowired
    private FansService fansService;
    @Autowired
    private UserService userService;

    /**
     * 关注用户
     * */
    @PostMapping("follow")
    public GraceJSONResult follow(@RequestParam String myId,
                                  @RequestParam String vlogerId){
        GraceJSONResult failed = FansErrorReturn(myId, vlogerId);
        if (failed != null) return failed;
        fansService.doFollow(myId,vlogerId);
        return GraceJSONResult.ok();
    }

    /**
     * 取消关注用户
     * */
    @PostMapping("cancel")
    public GraceJSONResult cancel(@RequestParam String myId,
                                  @RequestParam String vlogerId){
        GraceJSONResult failed = FansErrorReturn(myId, vlogerId);
        if (failed != null) return failed;
        fansService.doCancel(myId,vlogerId);
        return GraceJSONResult.ok();
    }

    /**
    * 返回myId，vlogerId为空错误信息
    */
    private GraceJSONResult FansErrorReturn(String myId, String vlogerId) {
        //先判断两个id不能为空
        if(StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)){
            return GraceJSONResult.error(ResponseStatusEnum.FAILED);
        }

        //判断当前用户不能关注自己
        if(myId.equalsIgnoreCase(vlogerId)){
            return GraceJSONResult.error(ResponseStatusEnum.FAILED);
        }

        //判断myId的用户以及vlogerId的用户是否存在
        if(userService.getUser(myId) == null){
            return GraceJSONResult.error(ResponseStatusEnum.SYSTEM_ERROR);
        }
        if(userService.getUser(vlogerId) == null){
            return GraceJSONResult.error(ResponseStatusEnum.SYSTEM_ERROR);
        }
        return null;
    }


    /**
     * 查找我关注的博主
     * */
    @GetMapping("queryDoIFollowVloger")
    public GraceJSONResult queryDoIFollowVloger(@RequestParam String myId,
                                                @RequestParam String vlogerId){
        return GraceJSONResult.ok(fansService.queryDoIFollowVloger(myId,vlogerId));
    }

    @GetMapping("queryMyFollows")
    public GraceJSONResult queryMyFollows(@RequestParam String myId,
                                          @RequestParam Integer page,
                                          @RequestParam Integer pageSize){
        PageSetUtil.setPage(page,pageSize);
        PagedGridResult pagedGridResult = fansService.queryMyFollows(myId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @GetMapping("queryMyFans")
    public GraceJSONResult queryMyFans(@RequestParam String myId,
                                          @RequestParam Integer page,
                                          @RequestParam Integer pageSize) {
        PageSetUtil.setPage(page,pageSize);
        PagedGridResult pagedGridResult = fansService.queryMyFans(myId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

}
