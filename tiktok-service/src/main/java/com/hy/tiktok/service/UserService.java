package com.hy.tiktok.service;

import com.hy.tiktok.bo.UpdateUsersBO;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.pojo.Users;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/19 8:37
 */
public interface UserService {
    /**
    * @Description: 查询手机号是否存在判断用户是否存在
    * @Param:
    * @param mobile:
    * @Return: com.hy.tiktok.pojo.Users
    */
    public Users queryMobileIsExist(String mobile);

    /**
    * @Description: 创建用户
    * @Param:
  * @param mobile:
    * @Return: com.hy.tiktok.pojo.Users
    */
    public Users createUser(String mobile);

    /**
    * @Description: 获取用户
    * @Param:
  * @param userId:
    * @Return: com.hy.tiktok.pojo.Users
    */
    public Users getUser(String userId);

}
