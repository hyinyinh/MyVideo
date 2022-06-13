package com.hy.tiktok.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.hy.tiktok.bo.RegistLoginBO;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.grace.ResponseStatusEnum;
import com.hy.tiktok.pojo.Users;
import com.hy.tiktok.service.LoginService;
import com.hy.tiktok.service.UserService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.utils.IPUtil;
import com.hy.tiktok.utils.SMSUtils;
import com.hy.tiktok.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/30 13:13
 */

@Service
@Slf4j
public class LoginServiceImpl extends BaseInfoProperties implements LoginService {
    @Autowired
    private SMSUtils smsUtils;
    @Autowired
    private UserService userService;

    @Override
    public void getSMSCode(String mobile, HttpServletRequest request) throws Exception {

        // 获取用户ip
        String userIp = IPUtil.getRequestIp(request);
        //根据用户ip进行限制 限制用户在60秒之内只能获取一次验证码
        redisOperator.setnx60s(MOBILE_SMSCODE+":"+userIp,userIp);

        //生成六位随机验证码
        String code = (int)((Math.random() * 9 +1) * 100000) +"";
        smsUtils.sendSMS(mobile,code);
        log.info(code);

        //把验证码放入redis中 用于后续的验证
        redisOperator.setnx60s(MOBILE_SMSCODE+":"+mobile,code);
    }

    @Override
    public GraceJSONResult login(RegistLoginBO registLoginBO, HttpServletRequest request) {
        String mobile = registLoginBO.getMobile();
        String verifyCode = registLoginBO.getSmsCode();
        //1.从redis中校验验证码是否正确
        String redisCode = redisOperator.get(MOBILE_SMSCODE + ":" + mobile);
        if(StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(verifyCode)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        //2.查询数据库是否存在该用户
        Users user = userService.queryMobileIsExist(mobile);
        if(user == null){
            //2.1用户为空 没注册过 创建用户
            user = userService.createUser(mobile);
        }
        //3.不为空 保存用户信息和会话信息
        String token = UUID.randomUUID().toString();

        String tokenKey = REDIS_USER_TOKEN+":"+user.getId();
        String userInfoToken = REDIS_USER_TOKEN+":"+token;
        String preToken = redisOperator.get(tokenKey);
        String preInfoToken = REDIS_USER_TOKEN+":"+preToken;

        if(preToken!=null){
            redisOperator.del(preToken);
            redisOperator.del(preInfoToken);
        }
        redisOperator.set(tokenKey,token);

        //将user转换为map存入token
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        redisOperator.hputAll(userInfoToken,userMap);

        //设置token有效期
        redisOperator.expire(tokenKey,LONING_USER_TTL);


        //4.用户登录注册成功后 删除redis中的短信验证码
        redisOperator.del(MOBILE_SMSCODE+":"+mobile);

        //5.返回用户信息 包含usertoken令牌信息
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        usersVO.setUserToken(token);
        return GraceJSONResult.ok(usersVO);
    }
}
