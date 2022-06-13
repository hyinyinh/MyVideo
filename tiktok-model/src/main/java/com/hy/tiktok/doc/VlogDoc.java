package com.hy.tiktok.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/3 11:21
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VlogDoc {
    String id;
    String vlogerName;
    String title;
    Integer count;
}
