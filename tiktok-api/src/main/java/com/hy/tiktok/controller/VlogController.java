package com.hy.tiktok.controller;

import com.hy.tiktok.bo.VlogBO;
import com.hy.tiktok.vo.HotVlogVO;
import com.hy.tiktok.enums.YesOrNo;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.service.VlogService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.utils.PageSetUtil;
import com.hy.tiktok.utils.PagedGridResult;
import com.hy.tiktok.vo.IndexVlogVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/22 14:49
 */
@Slf4j
@CrossOrigin
@Api(tags = "VlogController 短视频相关业务功能的接口")
@RequestMapping("vlog")
@RestController
public class VlogController extends BaseInfoProperties {
    @Autowired
    private VlogService vlogService;
    @Value("${nacos.counts}")
    private Integer nacosCounts;


    @PostMapping("publish")
    public GraceJSONResult publish(@RequestBody VlogBO vlogBO){
        //FIXME 检验VlogBO
        vlogService.createVlog(vlogBO);
        return GraceJSONResult.ok();
    }

    @GetMapping("indexList")
    public GraceJSONResult indexList(@RequestParam(defaultValue = "") String userId,
                                     @RequestParam(defaultValue = "") String search,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize){
        PageSetUtil.setPage(page,pageSize);

        PagedGridResult indexVlogList = vlogService.getIndexVlogList(userId,search,page,pageSize);
        return GraceJSONResult.ok(indexVlogList);
    }

    @GetMapping("detail")
    public GraceJSONResult getVlogDetailById(@RequestParam(defaultValue = "") String userId,
                                             @RequestParam String vlogId){
        IndexVlogVO vlog = vlogService.getHostVlogInfo(vlogId);
        if(vlog.getVlogId() == null){
            vlog = vlogService.getVlogDetailById(userId,vlogId);
        }
        return GraceJSONResult.ok(vlog);
    }

    @PostMapping("changeToPublic")
    public GraceJSONResult changeToPublic(@RequestParam String userId,
                                          @RequestParam String vlogId){
        vlogService.changeVlogToPublicOrPrivate(vlogId,userId, YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }

    @PostMapping("changeToPrivate")
    public GraceJSONResult changeToPrivate(@RequestParam String userId,
                                          @RequestParam String vlogId){
        vlogService.changeVlogToPublicOrPrivate(vlogId,userId, YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    @GetMapping("myPublicList")
    public GraceJSONResult myPublicList(@RequestParam String userId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize){
        if(page == null){
            page = COMMON_START_PAGE;
        }
        if(pageSize == null){
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult pagedGridResult = vlogService.queryMyVlogList(userId, page, pageSize, YesOrNo.NO.type);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @GetMapping("myPrivateList")
    public GraceJSONResult myPrivateList(@RequestParam String userId,
                                           @RequestParam Integer page,
                                           @RequestParam Integer pageSize){
        PageSetUtil.setPage(page,pageSize);
        PagedGridResult pagedGridResult = vlogService.queryMyVlogList(userId, page, pageSize, YesOrNo.YES.type);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @PostMapping("like")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId){
        vlogService.userLikeVlog(userId,vlogerId,vlogId);

        String countStr = redisOperator.get(REDIS_VLOG_BE_LIKED_COUNTS+":"+vlogId);
        Integer count = 0;
        if(StringUtils.isNotBlank(countStr)){
            count = Integer.valueOf(countStr);
        }
        if(count >= nacosCounts){
            vlogService.refreshLikeCount(vlogId,count);
        }
        return GraceJSONResult.ok();
    }



    @PostMapping("unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId){
        vlogService.userUnLikeVlog(userId,vlogId,vlogerId);
        return GraceJSONResult.ok();
    }

    @PostMapping("totalLikedCounts")
    public GraceJSONResult totalLikedCounts(@RequestParam String vlogId){
        return GraceJSONResult.ok(vlogService.getVlogBeLikedCounts(vlogId));
    }

    @GetMapping("followList")
    public GraceJSONResult followList(@RequestParam String myId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize){
        PageSetUtil.setPage(page,pageSize);

        PagedGridResult myFollowVlogList = vlogService.getMyFollowVlogList(myId, page, pageSize);
        return GraceJSONResult.ok(myFollowVlogList);
    }

    @GetMapping("friendList")
    public GraceJSONResult friendList(@RequestParam String myId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize){
        PageSetUtil.setPage(page,pageSize);

        PagedGridResult myFriendVlogList = vlogService.getMyFriendVlogList(myId, page, pageSize);
        return GraceJSONResult.ok(myFriendVlogList);
    }

    @GetMapping("hotList")
    public GraceJSONResult hotList(){
        List<HotVlogVO> hotVlogVOS = vlogService.queryHotVlog();
        return GraceJSONResult.ok(hotVlogVOS);
    }

    @GetMapping("hotVlogInfo")
    public GraceJSONResult hotListInfo(@RequestParam String vlogId){
        IndexVlogVO vlog = vlogService.getHostVlogInfo(vlogId);
        return GraceJSONResult.ok(vlog);
    }

}
