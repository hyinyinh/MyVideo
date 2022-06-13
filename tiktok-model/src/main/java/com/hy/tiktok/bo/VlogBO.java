package com.hy.tiktok.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/15 12:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VlogBO {
    private String vlogerId;
    @NotBlank(message = "vlog不能为空")
    private String url;
    @NotBlank(message = "视频封面不能为空")
    private String cover;
    private String title;
    private Integer width;
    private Integer height;
    private Integer likeCounts;
    private Integer commentsCounts;
    private Integer isPrivate;
    private Date createdTime;
    private Date updatedTime;

}
