package com.hy.tiktok.controller;

import com.hy.tiktok.bo.UpdateUsersBO;
import com.hy.tiktok.config.MinIOConfig;
import com.hy.tiktok.enums.FileTypeEnum;
import com.hy.tiktok.enums.UserInfoModifyType;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.grace.ResponseStatusEnum;
import com.hy.tiktok.pojo.Users;
import com.hy.tiktok.service.UserInfoService;
import com.hy.tiktok.service.common.BaseInfoProperties;
import com.hy.tiktok.service.common.UserHolder;
import com.hy.tiktok.utils.MinIOUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/20 19:39
 */

@Slf4j
@CrossOrigin
@Api(tags = "UserInfoController 用户信息接口模块")
@RequestMapping("userInfo")
@RestController
public class UserInfoController extends BaseInfoProperties {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private MinIOConfig minIOConfig;

    @GetMapping("query")
    public GraceJSONResult getUser(@RequestParam String userId){
        return userInfoService.queryUser(userId);
    }

    @PostMapping("modifyUserInfo")
    public GraceJSONResult modifyUserInfo(@RequestBody UpdateUsersBO updateUsersBO,
                                   @RequestParam Integer type){
        UserInfoModifyType.checkUserInfoTypeIsRight(type);
        Users userNew = userInfoService.updateUserInfo(updateUsersBO, type);
        return GraceJSONResult.ok(userNew);
    }

    @PostMapping("modifyImage")
    public GraceJSONResult upload(@RequestParam String userId,
                                  @RequestParam Integer type,
                                  MultipartFile file) throws Exception {
        return uploadFile(userId, type, file);
    }

    private GraceJSONResult uploadFile(String userId, Integer type, MultipartFile file) throws Exception {
        //http://192.168.139.129:9000/tiktok/8f9fe14c0be14b8f977a40e243a532e9%21400x400.jpg

        if(!type.equals(FileTypeEnum.BGIMG.type) && !type.equals(FileTypeEnum.FACE.type)){
            return GraceJSONResult.exception(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String fileName = file.getOriginalFilename();
        MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                fileName,
                file.getInputStream());

        String imgUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + fileName;

        UpdateUsersBO updateUsersBO = new UpdateUsersBO();
        updateUsersBO.setId(userId);
        if(type.equals(FileTypeEnum.BGIMG.type)){
            updateUsersBO.setBgImg(imgUrl);
        }else{
            updateUsersBO.setFace(imgUrl);
        }
        Users user = userInfoService.updateUserInfo(updateUsersBO);

        return GraceJSONResult.ok(user);
    }
}
