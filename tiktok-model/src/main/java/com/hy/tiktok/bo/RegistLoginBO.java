package com.hy.tiktok.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author hy
 * @version 1.0
 * @Desctiption 从前端过来的实体类
 * @date 2022/4/18 20:06
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegistLoginBO {

    @NotBlank(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "手机长度不正确")
    private String mobile;
    @NotBlank(message = "验证码不能为空")
    private String smsCode;

}
