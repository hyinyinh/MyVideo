package com.hy.tiktok.service;

import com.hy.tiktok.bo.UpdateUsersBO;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.pojo.Users;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/3 17:29
 */
public interface UserInfoService {
    public Users updateUserInfo(UpdateUsersBO updateUsersBO);
    public Users updateUserInfo(UpdateUsersBO updateUsersBO,Integer type);
    GraceJSONResult queryUser(String userId);
}
