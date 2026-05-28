package com.campus.lostfound.service;

import com.campus.lostfound.common.Constants;
import com.campus.lostfound.repository.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MatchRecordRepository matchRecordRepository;

    public StatsService(UserRepository userRepository, PostRepository postRepository,
                        MatchRecordRepository matchRecordRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.matchRecordRepository = matchRecordRepository;
    }

    public Map<String, Object> publicStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userRepository.count());
        stats.put("postCount", postRepository.countByModStatus(Constants.MOD_APPROVED));
        stats.put("lostCount", postRepository.countByTypeAndModStatus(Constants.POST_TYPE_LOST, Constants.MOD_APPROVED));
        stats.put("foundCount", postRepository.countByTypeAndModStatus(Constants.POST_TYPE_FOUND, Constants.MOD_APPROVED));
        stats.put("matchCount", matchRecordRepository.count());
        stats.put("claimedCount", postRepository.countByPostStatus(Constants.POST_STATUS_CLOSED));
        return stats;
    }
}
