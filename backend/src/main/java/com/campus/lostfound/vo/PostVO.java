package com.campus.lostfound.vo;

import com.campus.lostfound.entity.Post;
import com.campus.lostfound.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostVO {

    private Long id;
    private String type;
    private String title;
    private String category;
    private String location;
    private LocalDateTime eventTime;
    private String description;
    private String contact;
    private String imageUrl;
    private Long userId;
    private String publisherName;
    private String modStatus;
    private String postStatus;
    private Long pendingClaimId;
    private LocalDateTime createdAt;
    private long commentCount;
    private boolean favorited;

    public static PostVO from(Post post) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setType(post.getType());
        vo.setTitle(post.getTitle());
        vo.setCategory(post.getCategory());
        vo.setLocation(post.getLocation());
        vo.setEventTime(post.getEventTime());
        vo.setDescription(post.getDescription());
        vo.setContact(post.getContact());
        vo.setImageUrl(post.getImageUrl());
        vo.setUserId(post.getUserId());
        vo.setModStatus(post.getModStatus());
        vo.setPostStatus(post.getPostStatus());
        vo.setPendingClaimId(post.getPendingClaimId());
        vo.setCreatedAt(post.getCreatedAt());
        return vo;
    }

    public static PostVO from(Post post, User user) {
        PostVO vo = from(post);
        if (user != null) {
            vo.setPublisherName(user.getNickname());
        }
        return vo;
    }
}
