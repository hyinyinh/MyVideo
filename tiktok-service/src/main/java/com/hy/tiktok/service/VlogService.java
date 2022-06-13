package com.hy.tiktok.service;

import com.hy.tiktok.bo.VlogBO;
import com.hy.tiktok.vo.HotVlogVO;
import com.hy.tiktok.pojo.Vlog;
import com.hy.tiktok.utils.PagedGridResult;
import com.hy.tiktok.vo.IndexVlogVO;

import java.util.List;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/22 14:42
 */
public interface VlogService {
    void createVlog(VlogBO vlogBO);
    PagedGridResult getIndexVlogList(String userId,String search, Integer page, Integer pageSize);
    IndexVlogVO getVlogDetailById(String userId,String vlogId);
    void changeVlogToPublicOrPrivate(String vlogId,String userId,Integer isPrivate);
    PagedGridResult queryMyVlogList(String userId,Integer page,Integer pageSize,Integer isPrivate);
    /**
     * 实现用户的点赞功能
     * */
    void userLikeVlog(String userId,String vlogId,String vlogerId);
    void userUnLikeVlog(String userId,String vlogId,String vlogerId);
    Integer getVlogBeLikedCounts(String vlogId);
    PagedGridResult getMyFollowVlogList(String myId,Integer page,Integer pageSize);
    PagedGridResult getMyFriendVlogList(String myId,Integer page,Integer pageSize);
    Vlog getVlogById(String vlogId);
    void refreshLikeCount(String vlogId,Integer count);

    //查询热门视频top10
    List<HotVlogVO> queryHotVlog();

    IndexVlogVO getHostVlogInfo(String vlogId);
}
