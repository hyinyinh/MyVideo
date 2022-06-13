package com.hy.tiktok.intercepter;

import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.exceptions.GraceException;
import com.hy.tiktok.grace.ResponseStatusEnum;
import com.hy.tiktok.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/18 19:24
 */

@Slf4j
public class PassportInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIp = IPUtil.getRequestIp(request);
        if (redisOperator.keyIsExist(MOBILE_SMSCODE+":"+userIp)) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        return true;
    }

}
