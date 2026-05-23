package com.campus.lostfound.service;

import com.campus.lostfound.common.BusinessException;
import com.campus.lostfound.common.Constants;
import com.campus.lostfound.config.UserContext;
import com.campus.lostfound.entity.MatchRecord;
import com.campus.lostfound.entity.Post;
import com.campus.lostfound.repository.MatchRecordRepository;
import com.campus.lostfound.repository.PostRepository;
import com.campus.lostfound.vo.MatchVO;
import com.campus.lostfound.vo.PostVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private static final int SCORE_THRESHOLD = 60;

    private final MatchRecordRepository matchRecordRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    public MatchService(MatchRecordRepository matchRecordRepository,
                        PostRepository postRepository,
                        PostService postService) {
        this.matchRecordRepository = matchRecordRepository;
        this.postRepository = postRepository;
        this.postService = postService;
    }

    public List<MatchVO> forPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        List<MatchRecord> records;
        if (Constants.POST_TYPE_LOST.equals(post.getType())) {
            records = matchRecordRepository.findByLostPostIdOrderByScoreDesc(postId);
        } else {
            records = matchRecordRepository.findByFoundPostIdOrderByScoreDesc(postId);
        }

        if (records.isEmpty()) {
            return computeAndSaveForPost(post).stream()
                    .limit(10)
                    .map(this::toVO)
                    .collect(Collectors.toList());
        }

        return records.stream().map(this::toVO).collect(Collectors.toList());
    }

    public List<MatchVO> myMatches() {
        Long userId = UserContext.getUserId();
        List<Post> myPosts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        Set<Long> seen = new HashSet<>();
        List<MatchVO> result = new ArrayList<>();

        for (Post post : myPosts) {
            if (!Constants.MOD_APPROVED.equals(post.getModStatus())) {
                continue;
            }
            for (MatchVO vo : forPost(post.getId())) {
                if (seen.add(vo.getId())) {
                    result.add(vo);
                }
            }
        }

        result.sort((a, b) -> b.getScore().compareTo(a.getScore()));
        return result;
    }

    @Transactional
    public int runFullMatch() {
        List<Post> lostPosts = postRepository.findByTypeAndModStatus(
                Constants.POST_TYPE_LOST, Constants.MOD_APPROVED);
        List<Post> foundPosts = postRepository.findByTypeAndModStatus(
                Constants.POST_TYPE_FOUND, Constants.MOD_APPROVED);

        int count = 0;
        for (Post lost : lostPosts) {
            for (Post found : foundPosts) {
                int score = calculateScore(lost, found);
                if (score >= SCORE_THRESHOLD) {
                    saveMatch(lost.getId(), found.getId(), score);
                    count++;
                }
            }
        }
        return count;
    }

    public MatchVO acceptMatch(Long id) {
        MatchRecord record = getOwnedMatch(id);
        record.setStatus(Constants.MATCH_ACCEPTED);
        matchRecordRepository.save(record);
        return toVO(record);
    }

    public MatchVO dismissMatch(Long id) {
        MatchRecord record = getOwnedMatch(id);
        record.setStatus(Constants.MATCH_DISMISSED);
        matchRecordRepository.save(record);
        return toVO(record);
    }

    private MatchRecord getOwnedMatch(Long id) {
        MatchRecord record = matchRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("匹配记录不存在"));

        Long userId = UserContext.getUserId();
        Post lost = postRepository.findById(record.getLostPostId()).orElse(null);
        Post found = postRepository.findById(record.getFoundPostId()).orElse(null);

        boolean owned = (lost != null && lost.getUserId().equals(userId))
                || (found != null && found.getUserId().equals(userId))
                || Constants.ROLE_ADMIN.equals(UserContext.getRole());

        if (!owned) {
            throw new BusinessException(403, "无权操作此匹配");
        }
        return record;
    }

    private List<MatchRecord> computeAndSaveForPost(Post post) {
        List<Post> candidates;
        if (Constants.POST_TYPE_LOST.equals(post.getType())) {
            candidates = postRepository.findByTypeAndModStatus(
                    Constants.POST_TYPE_FOUND, Constants.MOD_APPROVED);
        } else {
            candidates = postRepository.findByTypeAndModStatus(
                    Constants.POST_TYPE_LOST, Constants.MOD_APPROVED);
        }

        List<MatchRecord> results = new ArrayList<>();
        for (Post candidate : candidates) {
            Long lostId = Constants.POST_TYPE_LOST.equals(post.getType()) ? post.getId() : candidate.getId();
            Long foundId = Constants.POST_TYPE_FOUND.equals(post.getType()) ? post.getId() : candidate.getId();
            Post lost = Constants.POST_TYPE_LOST.equals(post.getType()) ? post : candidate;
            Post found = Constants.POST_TYPE_FOUND.equals(post.getType()) ? post : candidate;

            int score = calculateScore(lost, found);
            if (score >= SCORE_THRESHOLD) {
                results.add(saveMatch(lostId, foundId, score));
            }
        }
        results.sort((a, b) -> b.getScore().compareTo(a.getScore()));
        return results;
    }

    private MatchRecord saveMatch(Long lostId, Long foundId, int score) {
        Optional<MatchRecord> existing = matchRecordRepository.findByLostPostIdAndFoundPostId(lostId, foundId);
        if (existing.isPresent()) {
            MatchRecord record = existing.get();
            record.setScore(score);
            return matchRecordRepository.save(record);
        }
        MatchRecord record = new MatchRecord();
        record.setLostPostId(lostId);
        record.setFoundPostId(foundId);
        record.setScore(score);
        record.setStatus(Constants.MATCH_SUGGESTED);
        return matchRecordRepository.save(record);
    }

    int calculateScore(Post lost, Post found) {
        int score = 0;

        if (lost.getCategory().equals(found.getCategory())) {
            score += 40;
        }

        score += (int) (jaccardSimilarity(lost.getLocation(), found.getLocation()) * 30);

        String lostText = lost.getTitle() + " " + lost.getDescription();
        String foundText = found.getTitle() + " " + found.getDescription();
        score += (int) (jaccardSimilarity(lostText, foundText) * 20);

        long hours = Math.abs(Duration.between(lost.getEventTime(), found.getEventTime()).toHours());
        if (hours <= 168) {
            score += (int) (10 * (1 - hours / 168.0));
        }

        return Math.min(score, 100);
    }

    private double jaccardSimilarity(String a, String b) {
        Set<String> setA = tokenize(a);
        Set<String> setB = tokenize(b);
        if (setA.isEmpty() || setB.isEmpty()) {
            return 0;
        }
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);
        return (double) intersection.size() / union.size();
    }

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptySet();
        }
        Set<String> tokens = new HashSet<>();
        for (String part : text.toLowerCase().split("[\\s,，。、；;]+")) {
            if (part.length() >= 2) {
                tokens.add(part);
            }
            for (int i = 0; i < part.length() - 1; i++) {
                tokens.add(part.substring(i, i + 2));
            }
        }
        return tokens;
    }

    private MatchVO toVO(MatchRecord record) {
        MatchVO vo = new MatchVO();
        vo.setId(record.getId());
        vo.setScore(record.getScore());
        vo.setStatus(record.getStatus());

        Post lost = postRepository.findById(record.getLostPostId()).orElse(null);
        Post found = postRepository.findById(record.getFoundPostId()).orElse(null);
        if (lost != null) {
            vo.setLostPost(postService.toVO(lost));
        }
        if (found != null) {
            vo.setFoundPost(postService.toVO(found));
        }
        vo.setReason(buildReason(lost, found, record.getScore()));
        return vo;
    }

    private String buildReason(Post lost, Post found, int score) {
        List<String> reasons = new ArrayList<>();
        if (lost != null && found != null && lost.getCategory().equals(found.getCategory())) {
            reasons.add("类别相同");
        }
        if (score >= 80) {
            reasons.add("描述高度相似");
        } else if (score >= 60) {
            reasons.add("地点或描述部分匹配");
        }
        return String.join("、", reasons);
    }
}
