package com.hy.tiktok.utils;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/30 14:22
 */
public class PageSetUtil{

    public static final Integer COMMON_START_PAGE = 1;
    public static final Integer COMMON_START_PAGE_ZERO = 0;
    public static final Integer COMMON_PAGE_SIZE = 10;

    public static void setPage(Integer page, Integer pageSize){
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
    }

}
