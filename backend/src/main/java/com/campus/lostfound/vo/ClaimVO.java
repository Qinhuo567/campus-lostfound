package com.campus.lostfound.vo;

import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClaimVO {

    private Long id;
    private Long postId;
    private Long userId;
    private String claimerName;
    private String proof;
    private String status;
    private LocalDateTime createdAt;

    public static ClaimVO from(Claim claim, User user) {
        ClaimVO vo = new ClaimVO();
        vo.setId(claim.getId());
        vo.setPostId(claim.getPostId());
        vo.setUserId(claim.getUserId());
        vo.setProof(claim.getProof());
        vo.setStatus(claim.getStatus());
        vo.setCreatedAt(claim.getCreatedAt());
        if (user != null) {
            vo.setClaimerName(user.getNickname());
        }
        return vo;
    }
}
