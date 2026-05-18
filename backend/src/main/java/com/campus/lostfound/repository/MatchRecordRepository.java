package com.campus.lostfound.repository;

import com.campus.lostfound.entity.MatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long> {

    List<MatchRecord> findByLostPostIdOrFoundPostIdOrderByScoreDesc(Long lostPostId, Long foundPostId);

    List<MatchRecord> findByLostPostIdOrderByScoreDesc(Long lostPostId);

    List<MatchRecord> findByFoundPostIdOrderByScoreDesc(Long foundPostId);

    Optional<MatchRecord> findByLostPostIdAndFoundPostId(Long lostPostId, Long foundPostId);

    long countByStatus(String status);
}
