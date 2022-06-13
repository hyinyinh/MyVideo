package com.hy.tiktok.service.Impl;

import com.hy.tiktok.enums.Sex;
import com.hy.tiktok.enums.YesOrNo;
import com.hy.tiktok.mapper.UsersMapper;
import com.hy.tiktok.pojo.Users;
import com.hy.tiktok.service.UserService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.utils.DateUtil;
import com.hy.tiktok.utils.DesensitizationUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/19 8:37
 */

@Service
public class UserServiceImpl extends BaseInfoProperties implements UserService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private Sid sid;
    private static final String USER_FACE1 = "https://github.com/hyinyinh/picture/blob/master/images/QQ%E5%9B%BE%E7%89%8720220418151026.jpg?raw=true";

    @Override
    public Users queryMobileIsExist(String mobile) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("mobile", mobile);
        Users user = usersMapper.selectOneByExample(userExample);
        return user;
    }

    @Override
    @Transactional
    public Users createUser(String mobile) {
        //获取全局唯一主键
        String userId = sid.nextShort();
        Users user = new Users();
        user.setId(userId);

        user.setMobile(mobile);
        user.setNickname("用户：" + DesensitizationUtil.commonDisplay(mobile));
        user.setImoocNum("用户：" + DesensitizationUtil.commonDisplay(mobile));
        user.setFace(USER_FACE1);

        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("这家伙很懒，什么都没留下~");
        user.setCanImoocNumBeUpdated(YesOrNo.YES.type);

        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);

        return user;
    }

    @Override
    public Users getUser(String userId) {
        Users user = usersMapper.selectByPrimaryKey(userId);
        return user;
    }


}
