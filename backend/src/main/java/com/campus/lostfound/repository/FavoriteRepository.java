package com.campus.lostfound.repository;

import com.campus.lostfound.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByPostIdAndUserId(Long postId, Long userId);

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
