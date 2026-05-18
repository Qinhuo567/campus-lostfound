package com.campus.lostfound.repository;

import com.campus.lostfound.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Claim> findByPostIdOrderByCreatedAtAsc(Long postId);

    Optional<Claim> findByIdAndPostId(Long id, Long postId);
}
