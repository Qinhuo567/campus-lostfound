package com.campus.lostfound.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_posts_type_mod", columnList = "type, mod_status"),
        @Index(name = "idx_posts_category", columnList = "category"),
        @Index(name = "idx_posts_user", columnList = "user_id")
})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String contact;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "mod_status", nullable = false, length = 20)
    private String modStatus = "pending";

    @Column(name = "post_status", nullable = false, length = 20)
    private String postStatus = "open";

    @Column(name = "pending_claim_id")
    private Long pendingClaimId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
