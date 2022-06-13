package com.hy.tiktok.enums;

/**
 * @Desc: 消息类型
 */
public enum MessageEnum {
    FOLLOW_YOU(1, "关注"),
    LIKE_VLOG(2, "点赞视频"),
    COMMENT_VLOG(3, "评论视频"),
    REPLY_YOU(4, "回复评论"),
    LIKE_COMMENT(5, "点赞评论"),
    UNFOLLOW_YOU(6, "取消关注"),
    UNLIKE_VLOG(7, "取消点赞视频"),
    UNCOMMENT_VLOG(8, "删除评论视频"),
    UNREPLY_YOU(9, "删除回复评论"),
    UNLIKE_COMMENT(10, "取消点赞评论");

    public final Integer type;
    public final String value;

    MessageEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
