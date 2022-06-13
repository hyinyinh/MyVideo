package com.hy.tiktok.redis;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Desctiption
 * @author  hy
 * @date  2022/5/15 18:00
 * @version 1.0
 */

@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
