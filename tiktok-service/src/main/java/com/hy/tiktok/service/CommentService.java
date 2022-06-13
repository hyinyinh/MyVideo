package com.hy.tiktok.service;

import com.hy.tiktok.bo.CommentBO;
import com.hy.tiktok.utils.PagedGridResult;
import com.hy.tiktok.vo.CommentVO;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/24 14:42
 */
public interface CommentService {
    CommentVO CreateComment(CommentBO commentBO);
    PagedGridResult getCommentList(String vlogId,String userId,Integer page,Integer pageSize);
    void deleteComment(String commentUserId,String commentId,String vlogId);

    void like(String commentId,String userId);
    void unlike(String commentId,String userId);

}
