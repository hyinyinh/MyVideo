package com.hy.tiktok.service;

import com.hy.tiktok.utils.PagedGridResult;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/23 17:23
 */

public interface FansService {
    /**
     * 关注博主 新建粉丝
    * */
    public void doFollow(String myId,String vlogerId);

    /**
     * 取消关注
     * */
    public void doCancel(String myId,String vlogerId);

    /**
     * 查询是否关注该用户
     * */
    public boolean queryDoIFollowVloger(String myId,String vlogerId);

    /**
     * 查询用户的关注列表
     * */
    public PagedGridResult queryMyFollows(String myId, Integer page, Integer pageSize);

    /**
     * 查询用户的粉丝列表
     * */
    public PagedGridResult queryMyFans(String myId, Integer page, Integer pageSize);


}
