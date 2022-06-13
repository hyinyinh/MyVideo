package com.hy.tiktok.intercepter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hy.tiktok.pojo.Users;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.service.common.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/3/22 13:05
 */

@Slf4j
public class RefreshTokenInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的token
        String token = request.getHeader("headerUserToken");
        String userId = request.getHeader("headerUserId");

        if (StrUtil.isBlank(token)) {
            return true;
        }
        // 2.基于TOKEN获取redis中的用户
        String userInfokey  = REDIS_USER_TOKEN+":"+token;
        String key  = REDIS_USER_TOKEN+":"+userId;

        String curToken = redisOperator.get(key);
        //3.判断用户是否重复登录
        if(!token.equals(curToken)){
            log.info("账号在别处登录，请重新登录");
            redisOperator.del(userInfokey);
            response.sendError(561);
            return false;
        }

        Map<Object,Object> map = redisOperator.hgetall(userInfokey);
        Users user = BeanUtil.fillBeanWithMap(map, new Users(), false);
        UserHolder.saveUser(user);
        // 7.刷新token有效期
        redisOperator.expire(key, LONING_USER_TTL);
        // 8.放行
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //移除用户 避免用户泄露
        UserHolder.removeUser();
    }

}
