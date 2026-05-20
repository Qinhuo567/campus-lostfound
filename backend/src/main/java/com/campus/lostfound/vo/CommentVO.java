package com.campus.lostfound.vo;

import com.campus.lostfound.entity.Comment;
import com.campus.lostfound.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {

    private Long id;
    private Long postId;
    private Long userId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;

    public static CommentVO from(Comment comment, User user) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setUserId(comment.getUserId());
        vo.setContent(comment.getContent());
        vo.setCreatedAt(comment.getCreatedAt());
        if (user != null) {
            vo.setNickname(user.getNickname());
        }
        return vo;
    }
}
