package com.campus.lostfound.service;

import com.campus.lostfound.config.UserContext;
import com.campus.lostfound.dto.CommentRequest;
import com.campus.lostfound.entity.Comment;
import com.campus.lostfound.entity.Favorite;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.repository.CommentRepository;
import com.campus.lostfound.repository.FavoriteRepository;
import com.campus.lostfound.repository.PostRepository;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.vo.CommentVO;
import com.campus.lostfound.vo.PostVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InteractionService {

    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;

    public InteractionService(CommentRepository commentRepository,
                              FavoriteRepository favoriteRepository,
                              PostRepository postRepository,
                              UserRepository userRepository,
                              PostService postService) {
        this.commentRepository = commentRepository;
        this.favoriteRepository = favoriteRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postService = postService;
    }

    public CommentVO addComment(Long postId, CommentRequest req) {
        if (!postRepository.existsById(postId)) {
            throw new com.campus.lostfound.common.BusinessException("帖子不存在");
        }
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(UserContext.getUserId());
        comment.setContent(req.getContent());
        commentRepository.save(comment);

        User user = userRepository.findById(UserContext.getUserId()).orElse(null);
        return CommentVO.from(comment, user);
    }

    public List<CommentVO> listComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(c -> CommentVO.from(c, userRepository.findById(c.getUserId()).orElse(null)))
                .collect(Collectors.toList());
    }

    public Map<String, Object> toggleFavorite(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new com.campus.lostfound.common.BusinessException("帖子不存在");
        }
        Long userId = UserContext.getUserId();
        var existing = favoriteRepository.findByPostIdAndUserId(postId, userId);
        boolean favorited;
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            favorited = false;
        } else {
            Favorite fav = new Favorite();
            fav.setPostId(postId);
            fav.setUserId(userId);
            favoriteRepository.save(fav);
            favorited = true;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("favorited", favorited);
        return result;
    }

    public List<PostVO> myFavorites() {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(UserContext.getUserId())
                .stream()
                .map(f -> postRepository.findById(f.getPostId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> postService.toVO(opt.get()))
                .collect(Collectors.toList());
    }
}
