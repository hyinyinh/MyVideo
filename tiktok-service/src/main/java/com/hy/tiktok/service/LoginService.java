package com.hy.tiktok.service;

import com.hy.tiktok.bo.RegistLoginBO;
import com.hy.tiktok.grace.GraceJSONResult;
import com.hy.tiktok.vo.UsersVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/30 13:12
 */
public interface LoginService {
    public void getSMSCode(String mobile, HttpServletRequest request) throws Exception;
    public GraceJSONResult login(RegistLoginBO registLoginBO, HttpServletRequest request);
}
