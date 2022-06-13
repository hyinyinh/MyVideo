package com.hy.tiktok.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/14 19:12
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotVlogVO {
    String vlogId;
    String title;
    Integer likeCounts;
}
