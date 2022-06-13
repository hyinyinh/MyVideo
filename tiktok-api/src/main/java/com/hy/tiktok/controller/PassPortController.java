package com.hy.tiktok.controller;

import com.hy.tiktok.service.common.UserHolder;
import com.hy.tiktok.bo.RegistLoginBO;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.service.LoginService;
import com.hy.tiktok.service.UserService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/18 15:29
 */

@Slf4j
@CrossOrigin
@Api(tags = "PassPortController 通行证接口模块")
@RestController
@RequestMapping("/passport")
public class PassPortController extends BaseInfoProperties {
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;

    @ApiOperation("短信发送验证码")
    @PostMapping("/getSMSCode")
    public Object getSMSCode(@RequestParam String mobile,
                        HttpServletRequest request) throws Exception{
        if(StringUtils.isBlank(mobile)){
            return GraceJSONResult.ok();
        }
        loginService.getSMSCode(mobile, request);
        return GraceJSONResult.ok();
    }

    @ApiOperation("验证登录手机号以及验证码是否非法输入")
    @PostMapping("login")
    public GraceJSONResult login(@Valid @RequestBody RegistLoginBO registLoginBO,
                                      HttpServletRequest request){
        return loginService.login(registLoginBO, request);
    }

    @ApiOperation("用户退出登录模块")
    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam String userId){
        //后端只需要清除token
        //前端清除用户信息和token
        redisOperator.del(REDIS_USER_TOKEN+":"+userId);
        UserHolder.removeUser();
        return GraceJSONResult.ok();
    }


}
