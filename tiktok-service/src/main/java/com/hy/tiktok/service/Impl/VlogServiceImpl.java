package com.hy.tiktok.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.hy.tiktok.bo.VlogBO;
import com.hy.tiktok.enums.YesOrNo;
import com.hy.tiktok.mapper.UsersMapper;
import com.hy.tiktok.pojo.Users;
import com.hy.tiktok.redis.RedisData;
import com.hy.tiktok.vo.HotVlogVO;
import com.hy.tiktok.enums.MessageEnum;
import com.hy.tiktok.mapper.MyLikedVlogMapper;
import com.hy.tiktok.mapper.VlogMapper;
import com.hy.tiktok.mapper.VlogMapperCustom;
import com.hy.tiktok.pojo.MyLikedVlog;
import com.hy.tiktok.pojo.Vlog;
import com.hy.tiktok.service.FansService;
import com.hy.tiktok.service.MsgService;
import com.hy.tiktok.service.SendService;
import com.hy.tiktok.service.VlogService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.utils.PagedGridResult;
import com.hy.tiktok.vo.IndexVlogVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/22 14:43
 */
@Service
public class VlogServiceImpl extends BaseInfoProperties implements VlogService{
    @Autowired
    private VlogMapper vlogMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private VlogMapperCustom vlogMapperCustom;
    @Autowired
    private MyLikedVlogMapper myLikedVlogMapper;
    @Autowired
    private FansService fansService;
    @Autowired
    private MsgService msgService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SendService sendService;
    @Autowired
    private UsersMapper usersMapper;

    /**
     * 创建视频
     * */
    @Override
    public void createVlog(VlogBO vlogBO) {
        String vid = sid.nextShort();
        Vlog vlog = new Vlog();
        BeanUtils.copyProperties(vlogBO,vlog);
        vlog.setId(vid);
        vlog.setCommentsCounts(0);
        vlog.setLikeCounts(0);
        vlog.setIsPrivate(0);
        vlog.setCreatedTime(new Date());
        vlog.setUpdatedTime(new Date());
        vlogMapper.insert(vlog);
    }

    /**
     * 获取首页信息视频
     * */
    @Override
    public PagedGridResult getIndexVlogList (String userId,
                                             String search,
                                             Integer page,
                                             Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isNotBlank(search)){
            map.put("search",search);
        }
        List<IndexVlogVO> indexVlog = vlogMapperCustom.getIndexVlogList(map);

        for (IndexVlogVO vlog : indexVlog) {
            setterVO(vlog,userId);
        }
        return setterPagedGrid(indexVlog,page);
    }

    //获取该视频点赞数量
    public Integer getVlogBeLikedCounts(String vlogId) {
        String countStr = redisOperator.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        if(StringUtils.isNotBlank(countStr)){
            return Integer.valueOf(countStr);
        }else{
            return 0;
        }
    }

    @Override
    public PagedGridResult getMyFollowVlogList(String myId,Integer page,Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("myId",myId);
        List<IndexVlogVO> myFollowVlogList = vlogMapperCustom.getMyFollowOrFriendVlogList(map);
        PageHelper.startPage(page,pageSize);
        for (IndexVlogVO vlog : myFollowVlogList) {
            setterVO(vlog,myId);
        }
        return setterPagedGrid(myFollowVlogList,page);
    }

    @Override
    public PagedGridResult getMyFriendVlogList(String myId, Integer page, Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("myId",myId);
        map.put("isFanFriendOfMine",1);
        List<IndexVlogVO> myFollowVlogList = vlogMapperCustom.getMyFollowOrFriendVlogList(map);
        PageHelper.startPage(page,pageSize);
        for (IndexVlogVO vlog : myFollowVlogList) {
            String vlogId = vlog.getVlogId();

            if(StringUtils.isNotBlank(myId)){
                //用户一定关注该博主
                vlog.setDoIFollowVloger(true);
                //判断当前用户是否点赞过该视频
                vlog.setDoILikeThisVlog(doILikeVlog(myId, vlogId));
            }
            //获取当前视频被点赞次数
            vlog.setLikeCounts(getVlogBeLikedCounts(vlogId));
        }
        return setterPagedGrid(myFollowVlogList,page);
    }

    @Override
    public Vlog getVlogById(String vlogId) {
        return vlogMapper.selectByPrimaryKey(vlogId);
    }

    public boolean doILikeVlog(String myId,String vlogId){
        String doILike = redisOperator.get(REDIS_USER_LIKE_VLOG + ":" + myId + ":" + vlogId);
        boolean isLike = false;
        if (StringUtils.isNotBlank(doILike) && doILike.equalsIgnoreCase("1")) {
            isLike = true;
        }
        return isLike;
    }

    @Override
    public IndexVlogVO getVlogDetailById(String userId,String vlogId) {
        Map<String,Object> map = new HashMap<>();
        map.put("vlogId",vlogId);
        List<IndexVlogVO> list = vlogMapperCustom.getVlogDetailById(map);
        //拿到一个list一定要判断是否为空
        if(list != null && list.size() > 0){
            IndexVlogVO vlogVO = list.get(0);
            return setterVO(vlogVO,userId);
        }
        return null;
    }

    @Transactional
    @Override
    public void changeVlogToPublicOrPrivate(String vlogId, String userId, Integer isPrivate) {
        //缓存一致性：先更新数据库 再删除缓存

        //创建匹配条件
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",vlogId);
        criteria.andEqualTo("vlogerId",userId);

        Vlog vlog = new Vlog();
        vlog.setIsPrivate(isPrivate);

        //匹配example的条件 选择性更新vlog中设置的条件
        vlogMapper.updateByExampleSelective(vlog,example);

        if(isPrivate==1){
            redisOperator.zSetRemove(SORT_VLOG_LIST,vlogId);
            redisOperator.hdel(HOST_VLOG_LIST+":"+vlogId);
        }
    }

    @Override
    public PagedGridResult queryMyVlogList(String userId, Integer page, Integer pageSize, Integer isPrivate) {
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId",userId);
        criteria.andEqualTo("isPrivate",isPrivate);
        PageHelper.startPage(page,pageSize);
        List<Vlog> vlogs = vlogMapper.selectByExample(example);
        return setterPagedGrid(vlogs,page);
    }

    @Override
    @Transactional
    public void userLikeVlog(String userId, String vlogerId,String vlogId) {
        String vid = sid.nextShort();
        MyLikedVlog myLikedVlog = new MyLikedVlog();
        myLikedVlog.setId(vid);
        myLikedVlog.setUserId(userId);
        myLikedVlog.setVlogId(vlogId);
        myLikedVlogMapper.insert(myLikedVlog);

        Vlog vlog = getVlogById(vlogId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("vlogId",vlogId);
        map.put("vlogCover",vlog.getCover());

        sendService.sendMessage(vlogerId,userId,map,MessageEnum.LIKE_VLOG.type);

        //点赞后 视频和视频发布者的获赞都 +1
        redisOperator.increment(REDIS_VLOG_BE_LIKED_COUNTS+":"+vlogId,1);
        redisOperator.increment(REDIS_VLOGER_BE_LIKED_COUNTS+":"+vlogerId,1);
        //我点赞的视频在redis中保存关联关系
        redisOperator.set(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId, "1");

        addHostList(vlogId,vlogerId);
    }

    @Override
    public void userUnLikeVlog(String userId, String vlogId, String vlogerId) {
        MyLikedVlog myLikedVlog = new MyLikedVlog();
        myLikedVlog.setUserId(userId);
        myLikedVlog.setVlogId(vlogId);
        myLikedVlogMapper.delete(myLikedVlog);

        redisOperator.decrement(REDIS_VLOG_BE_LIKED_COUNTS+":"+vlogId,1);
        redisOperator.decrement(REDIS_VLOGER_BE_LIKED_COUNTS+":"+vlogerId,1);
        //我点赞的视频在redis中保存关联关系
        redisOperator.del(REDIS_USER_LIKE_VLOG+":"+userId+":"+vlogId);

        Vlog vlog = getVlogById(vlogId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("vlogId",vlogId);
        map.put("vlogCover",vlog.getCover());

        sendService.sendMessage(vlogerId,userId,map,MessageEnum.UNLIKE_VLOG.type);

        String curCountStr = redisOperator.get(REDIS_VLOG_BE_LIKED_COUNTS+":"+vlogId);
        int curCount = 0;
        if(StringUtils.isNotBlank(curCountStr)){
            curCount = Integer.valueOf(curCountStr);
        }

        //排行榜中有该数据
        if(redisOperator.getZsetScore(SORT_VLOG_LIST,vlogId)!=null){
            if(curCount > 0){
                redisOperator.zSetAdd(SORT_VLOG_LIST,vlogId, (double) -toScore(curCount));
            }else{
                redisOperator.zSetRemove(SORT_VLOG_LIST,vlogId);
            }
        }
    }

    private IndexVlogVO setterVO(IndexVlogVO vlog,String userId){
        String vlogId = vlog.getVlogId();
        String vlogerId = vlog.getVlogerId();

        if(StringUtils.isNotBlank(userId)){
            //判断当前用户是否关注该博主
            boolean doIFollowVloger = fansService.queryDoIFollowVloger(userId, vlogerId);
            vlog.setDoIFollowVloger(doIFollowVloger);

            //判断当前用户是否点赞过该视频
            vlog.setDoILikeThisVlog(doILikeVlog(userId, vlogId));
        }
        //获取当前视频被点赞次数
        vlog.setLikeCounts(getVlogBeLikedCounts(vlogId));
        return vlog;
    }

    @Override
    public void refreshLikeCount(String vlogId,Integer count){
        Vlog vlog = new Vlog();
        vlog.setId(vlogId);
        vlog.setLikeCounts(count);

        vlogMapper.updateByPrimaryKeySelective(vlog);
    }

    @Override
    public List<HotVlogVO> queryHotVlog() {
        Set<String> top10 = redisOperator.getZsetRange(SORT_VLOG_LIST, 0L, 9L);
        List<String> vlogids = new ArrayList<>(top10);
        if(top10==null || top10.isEmpty()){
            return Collections.emptyList();
        }

        List<HotVlogVO> vlogs = new ArrayList<>();

        //3.根据用户id查询视频
        for(String id : vlogids) {
            String likeCounts = redisOperator.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + id);
            if (redisOperator.hgetall(HOST_VLOG_LIST + ":" + id).isEmpty()) {
                Vlog vlog1 = vlogMapper.selectByPrimaryKey(id);

                HashMap<String, Object> map = new HashMap<>();
                map.put("vlogId", id);
                IndexVlogVO vlog = vlogMapperCustom.getVlogDetailById(map).get(0);

                //将vlog转换为map存入redis
                Map<String, Object> vlogMap = BeanUtil.beanToMap(vlog, new HashMap<>(),
                        CopyOptions.create()
                                .ignoreNullValue()
                                .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

                RedisData redisData = new RedisData();
                redisData.setData(map);

                redisOperator.hputAll(HOST_VLOG_LIST + ":" + id, vlogMap);
                redisOperator.expire(HOST_VLOG_LIST + ":" + id, 15 * 60L);
            }
            HotVlogVO hotVlogVO = new HotVlogVO();
            String title = redisOperator.hget(HOST_VLOG_LIST + ":" + id, "content");
            hotVlogVO.setVlogId(id);
            hotVlogVO.setTitle(title);
            hotVlogVO.setLikeCounts(Integer.valueOf(likeCounts));

            vlogs.add(hotVlogVO);
        }

        //4.返回
        return vlogs;
    }

    @Override
    public IndexVlogVO getHostVlogInfo(String vlogId) {
        Map<Object, Object> map = redisOperator.hgetall(HOST_VLOG_LIST+":"+vlogId);
        IndexVlogVO vlogBO = null;
        if(map != null){
            vlogBO = JSON.parseObject(JSON.toJSONString(map), IndexVlogVO.class);
        }
        return vlogBO;
    }

    //生成排行榜分数
    //1位高位不用 + 22位表示积分 + 41位时间戳
    // periodEndTimestamp: 当前周期结束时间的时间戳
    // 需确保point不会超过22bit所能表示的数值：2097151
    private static long toScore(int point) {
        long score = 0L;
        score = (score | point) << 41;
        score = score | System.currentTimeMillis();
        return score;
    }

    public void addHostList(String vlogId,String vlogerId){
        //生成排行榜分数
        long score = toScore(Integer.valueOf(redisOperator.get(REDIS_VLOG_BE_LIKED_COUNTS+":"+vlogId)));
        //当前redis sortlist已经放满
        if(redisOperator.getZSetSize(SORT_VLOG_LIST) >= HOST_LIST_MAXCOUNT){
            //获取redis排行榜的最后一个数据
            Set<String> zSetLast = redisOperator.getZSetLast(SORT_VLOG_LIST);
            //转换为list集合
            List<String> collect = new ArrayList<>(zSetLast);
            if(collect!=null){
                //查看当前分数
                Double zsetScore = redisOperator.getZsetScore(SORT_VLOG_LIST, collect.get(0));
                //榜尾比当前要插入的视频分数小 视频插入榜中
                if(zsetScore < score){
                    //删除最后一个
                    redisOperator.zSetRemove(SORT_VLOG_LIST,collect.get(0));
                    redisOperator.zSetAdd(SORT_VLOG_LIST,vlogId, (double) -score);
                    redisOperator.expire(SORT_VLOG_LIST,HOT_VLOG_TTL);
                }
            }
        }else{
            //sortlist还未放满 将点赞视频数加入到set列表
            redisOperator.zSetAdd(SORT_VLOG_LIST,vlogId, (double) -score);
            redisOperator.expire(SORT_VLOG_LIST,HOT_VLOG_TTL);
        }

    }



}
