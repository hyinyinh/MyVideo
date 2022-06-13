package com.hy.tiktok.mapper;


import com.hy.tiktok.my.mapper.MyMapper;
import com.hy.tiktok.pojo.Fans;
import com.hy.tiktok.vo.FansVO;
import com.hy.tiktok.vo.VlogerVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FansMapperCustom extends MyMapper<Fans> {
    List<VlogerVO> queryMyFollows(@Param("paraMap") Map<String,Object> map);
    List<FansVO> queryMyFans(@Param("paraMap") Map<String,Object> map);
}