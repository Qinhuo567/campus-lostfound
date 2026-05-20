package com.campus.lostfound.service;

import com.campus.lostfound.common.BusinessException;
import com.campus.lostfound.common.Constants;
import com.campus.lostfound.config.UserContext;
import com.campus.lostfound.dto.ClaimRequest;
import com.campus.lostfound.dto.PostRequest;
import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.entity.Post;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.repository.ClaimRepository;
import com.campus.lostfound.repository.CommentRepository;
import com.campus.lostfound.repository.FavoriteRepository;
import com.campus.lostfound.repository.PostRepository;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.vo.ClaimVO;
import com.campus.lostfound.vo.PostVO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ClaimRepository claimRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository,
                       ClaimRepository claimRepository, CommentRepository commentRepository,
                       FavoriteRepository favoriteRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.claimRepository = claimRepository;
        this.commentRepository = commentRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public List<PostVO> listPosts(String type, String category, String keyword, String postStatus) {
        Specification<Post> spec = (root, query, cb) ->
                cb.equal(root.get("modStatus"), Constants.MOD_APPROVED);

        if (type != null && !type.isBlank() && !"all".equals(type)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (category != null && !category.isBlank() && !"all".equals(category)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (postStatus != null && !postStatus.isBlank() && !"all".equals(postStatus)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("postStatus"), postStatus));
        }

        List<Post> posts = postRepository.findAll(spec);
        posts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return posts.stream()
                .filter(p -> matchKeyword(p, keyword))
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    public PostVO getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException("帖子不存在"));
        if (!Constants.MOD_APPROVED.equals(post.getModStatus())
                && !post.getUserId().equals(UserContext.getUserId())
                && !Constants.ROLE_ADMIN.equals(UserContext.getRole())) {
            throw new BusinessException("帖子不存在或未通过审核");
        }
        return toVO(post);
    }

    public PostVO createPost(PostRequest req) {
        validatePostType(req.getType());

        Post post = new Post();
        post.setType(req.getType());
        post.setTitle(req.getTitle());
        post.setCategory(req.getCategory());
        post.setLocation(req.getLocation());
        post.setEventTime(req.getEventTime() != null ? req.getEventTime() : LocalDateTime.now());
        post.setDescription(req.getDescription());
        post.setContact(req.getContact());
        post.setImageUrl(req.getImageUrl());
        post.setUserId(UserContext.getUserId());
        post.setModStatus(Constants.MOD_PENDING);
        post.setPostStatus(Constants.POST_STATUS_OPEN);
        postRepository.save(post);
        return toVO(post);
    }

    public List<PostVO> myPosts() {
        return postRepository.findByUserIdOrderByCreatedAtDesc(UserContext.getUserId())
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    public PostVO updatePost(Long id, PostRequest req) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException("帖子不存在"));
        if (!post.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException(403, "只能编辑自己的帖子");
        }

        post.setTitle(req.getTitle());
        post.setCategory(req.getCategory());
        post.setLocation(req.getLocation());
        if (req.getEventTime() != null) {
            post.setEventTime(req.getEventTime());
        }
        post.setDescription(req.getDescription());
        post.setContact(req.getContact());
        post.setImageUrl(req.getImageUrl());
        post.setModStatus(Constants.MOD_PENDING);
        postRepository.save(post);
        return toVO(post);
    }

    @Transactional
    public ClaimVO createClaim(Long postId, ClaimRequest req) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        if (!Constants.POST_TYPE_FOUND.equals(post.getType())) {
            throw new BusinessException("仅招领帖可发起认领");
        }
        if (!Constants.MOD_APPROVED.equals(post.getModStatus())) {
            throw new BusinessException("帖子未通过审核");
        }
        if (!Constants.POST_STATUS_OPEN.equals(post.getPostStatus())) {
            throw new BusinessException("该物品当前不可认领");
        }
        if (post.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException("不能认领自己发布的物品");
        }

        Claim claim = new Claim();
        claim.setPostId(postId);
        claim.setUserId(UserContext.getUserId());
        claim.setProof(req.getProof());
        claim.setStatus(Constants.CLAIM_PENDING);
        claimRepository.save(claim);

        post.setPostStatus(Constants.POST_STATUS_PENDING_CLAIM);
        post.setPendingClaimId(claim.getId());
        postRepository.save(post);

        User claimer = userRepository.findById(UserContext.getUserId()).orElse(null);
        return ClaimVO.from(claim, claimer);
    }

    @Transactional
    public PostVO approveClaim(Long postId, Long claimId) {
        Post post = getOwnedPost(postId);
        Claim claim = claimRepository.findByIdAndPostId(claimId, postId)
                .orElseThrow(() -> new BusinessException("认领记录不存在"));

        if (!Constants.POST_STATUS_PENDING_CLAIM.equals(post.getPostStatus())
                || !claimId.equals(post.getPendingClaimId())) {
            throw new BusinessException("认领状态已变更");
        }

        claim.setStatus(Constants.CLAIM_APPROVED);
        claimRepository.save(claim);
        post.setPostStatus(Constants.POST_STATUS_CLOSED);
        post.setPendingClaimId(null);
        postRepository.save(post);
        return toVO(post);
    }

    @Transactional
    public PostVO rejectClaim(Long postId, Long claimId) {
        Post post = getOwnedPost(postId);
        Claim claim = claimRepository.findByIdAndPostId(claimId, postId)
                .orElseThrow(() -> new BusinessException("认领记录不存在"));

        if (!Constants.POST_STATUS_PENDING_CLAIM.equals(post.getPostStatus())
                || !claimId.equals(post.getPendingClaimId())) {
            throw new BusinessException("认领状态已变更");
        }

        claim.setStatus(Constants.CLAIM_REJECTED);
        claimRepository.save(claim);
        post.setPostStatus(Constants.POST_STATUS_OPEN);
        post.setPendingClaimId(null);
        postRepository.save(post);
        return toVO(post);
    }

    public List<PostVO> myClaims() {
        List<Long> postIds = claimRepository.findByUserIdOrderByCreatedAtDesc(UserContext.getUserId())
                .stream()
                .map(Claim::getPostId)
                .distinct()
                .collect(Collectors.toList());

        return postIds.stream()
                .map(postRepository::findById)
                .filter(opt -> opt.isPresent())
                .map(opt -> toVO(opt.get()))
                .collect(Collectors.toList());
    }

    public List<ClaimVO> getPostClaims(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));
        if (!post.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException(403, "无权查看");
        }
        return claimRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(c -> ClaimVO.from(c, userRepository.findById(c.getUserId()).orElse(null)))
                .collect(Collectors.toList());
    }

    public PostVO toVO(Post post) {
        User user = userRepository.findById(post.getUserId()).orElse(null);
        PostVO vo = PostVO.from(post, user);
        vo.setCommentCount(commentRepository.countByPostId(post.getId()));
        Long currentUserId = UserContext.getUserId();
        if (currentUserId != null) {
            vo.setFavorited(favoriteRepository.existsByPostIdAndUserId(post.getId(), currentUserId));
        }
        return vo;
    }

    private Post getOwnedPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));
        if (!post.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException(403, "仅发布者可操作");
        }
        return post;
    }

    private boolean matchKeyword(Post post, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String k = keyword.toLowerCase();
        return post.getTitle().toLowerCase().contains(k)
                || post.getLocation().toLowerCase().contains(k)
                || post.getDescription().toLowerCase().contains(k);
    }

    private void validatePostType(String type) {
        if (!Constants.POST_TYPE_LOST.equals(type) && !Constants.POST_TYPE_FOUND.equals(type)) {
            throw new BusinessException("帖子类型无效");
        }
    }
}
