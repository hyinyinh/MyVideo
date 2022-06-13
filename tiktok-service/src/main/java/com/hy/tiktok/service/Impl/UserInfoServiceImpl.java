package com.hy.tiktok.service.Impl;

import com.hy.tiktok.service.common.UserHolder;
import com.hy.tiktok.bo.UpdateUsersBO;
import com.hy.tiktok.enums.UserInfoModifyType;
import com.hy.tiktok.enums.YesOrNo;
import com.hy.tiktok.exceptions.GraceException;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.grace.ResponseStatusEnum;
import com.hy.tiktok.mapper.UsersMapper;
import com.hy.tiktok.pojo.Users;
import com.hy.tiktok.service.UserInfoService;
import com.hy.tiktok.service.UserService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/3 17:31
 */
@Service
@Slf4j
public class UserInfoServiceImpl extends BaseInfoProperties implements UserInfoService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UserService userService;


    @Override
    @Transactional
    public Users updateUserInfo(UpdateUsersBO updateUsersBO) {
        Users user = new Users();
        BeanUtils.copyProperties(updateUsersBO,user);

        //updateByPrimaryKeySelective 只修改有值的数值 不修改为空的数值
        //updateByExample 直接覆盖原来空值
        int result = usersMapper.updateByPrimaryKeySelective(user);
        if(result != 1){
            GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);
        }
        return userService.getUser(updateUsersBO.getId());
    }

    @Transactional
    @Override
    public Users updateUserInfo(UpdateUsersBO updateUsersBO, Integer type) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        if(type.equals(UserInfoModifyType.NICKNAME.type)){
            criteria.andEqualTo("nickname",updateUsersBO.getNickname());
            Users user = usersMapper.selectOneByExample(example);
            if(user != null){
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }
        }
        if(type.equals(UserInfoModifyType.IMOOCNUM.type)){
            criteria.andEqualTo("imoocNum",updateUsersBO.getImoocNum());
            Users user = usersMapper.selectOneByExample(example);
            if(user != null){
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_IMOOCNUM_EXIST_ERROR);
            }
            Users tempUser = userService.getUser(updateUsersBO.getId());
            if(tempUser.getCanImoocNumBeUpdated().equals(YesOrNo.NO.type)){
                GraceException.display(ResponseStatusEnum.USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR);
            }
            updateUsersBO.setCanImoocNumBeUpdated(YesOrNo.NO.type);
        }
        return updateUserInfo(updateUsersBO);
    }

    @Override
    public GraceJSONResult queryUser(String userId) {

        Users me = UserHolder.getUser();
        Users user = null;
        if(me!=null && userId.equals(me.getId())){
            user = me;
        }else{
            user = userService.getUser(userId);
        }

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);

        //我的关注博主总数
        String myFollowCountsStr = redisOperator.get(REDIS_MY_FOLLOWS_COUNTS + ":" + userId);
        //我的粉丝总数
        String myFansCountsStr = redisOperator.get(REDIS_MY_FANS_COUNTS + ":" + userId);
        //用户获赞总数
        String likedVlogerCountsStr = redisOperator.get(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + userId);

        Integer myFollowsCounts = 0;
        Integer myFansCounts = 0;
        Integer likedVlogerCounts = 0;

        if(StringUtils.isNotBlank(myFollowCountsStr)){
            myFollowsCounts = Integer.valueOf(myFollowCountsStr);
        }
        if(StringUtils.isNotBlank(myFansCountsStr)){
            myFansCounts = Integer.valueOf(myFansCountsStr);
        }
        if(StringUtils.isNotBlank(likedVlogerCountsStr)){
            likedVlogerCounts = Integer.valueOf(likedVlogerCountsStr);
        }

        usersVO.setMyFansCounts(myFansCounts);
        usersVO.setMyFollowsCounts(myFollowsCounts);
        usersVO.setTotalLikeMeCounts(likedVlogerCounts);

        return GraceJSONResult.ok(usersVO);
    }


}
