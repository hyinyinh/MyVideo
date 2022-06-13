package com.hy.tiktok.mapper;


import com.hy.tiktok.vo.IndexVlogVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VlogMapperCustom{
    /**
    * 获取首页视频列表
    * */
    List<IndexVlogVO> getIndexVlogList(@Param("paramMap") Map<String, Object> map);

    /**
     * @return 根据视频id获取视频列表
     * 用途：查询出来的视频 点进去看显示视频详情
     */
    List<IndexVlogVO> getVlogDetailById(@Param("paramMap") Map<String, Object> map);

    /**
     * 用户关注的博主或者用户的朋友的视频
     * */
    List<IndexVlogVO> getMyFollowOrFriendVlogList(@Param("paramMap") Map<String, Object> map);
}