package com.campus.lostfound.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "matches", indexes = {
        @Index(name = "idx_matches_lost", columnList = "lost_post_id"),
        @Index(name = "idx_matches_found", columnList = "found_post_id")
})
public class MatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lost_post_id", nullable = false)
    private Long lostPostId;

    @Column(name = "found_post_id", nullable = false)
    private Long foundPostId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false, length = 20)
    private String status = "suggested";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
