package com.hy.tiktok.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
/**
* @Description: 后端传入前端的实体类
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsersVO {
    private String id;
    private String mobile;
    private String nickname;
    private String imoocNum;
    private String face;
    private Integer sex;
    private Date birthday;
    private String country;
    private String province;
    private String city;
    private String district;
    private String description;
    private String bgImg;
    private Integer canImoocNumBeUpdated;
    private Date createdTime;
    private Date updatedTime;

    private String userToken;  //用户token 传递给前端

    private Integer myFollowsCounts;  //我关注博主总数
    private Integer myFansCounts;  //我的粉丝总数
    private Integer myLikedVlogCounts;  //喜欢我的视频的总数
    private Integer totalLikeMeCounts;  //喜欢我的总数

}