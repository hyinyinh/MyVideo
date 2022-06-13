package com.hy.tiktok.controller;

import com.hy.tiktok.config.MinIOConfig;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.utils.MinIOUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/21 17:10
 */

@Slf4j
@CrossOrigin
@Api(tags = "FileController 文件上传测试的接口")
@RestController
public class FileController {

    @Autowired
    private MinIOConfig minIOConfig;

    @PostMapping("upload")
    public GraceJSONResult upload(MultipartFile file) throws Exception {

        String fileName = file.getOriginalFilename();

        MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                              fileName,
                              file.getInputStream());

        String imgUrl = minIOConfig.getFileHost()
                        + "/"
                        + minIOConfig.getBucketName()
                        + "/"
                        + fileName;

        return GraceJSONResult.ok(imgUrl);
    }
}
