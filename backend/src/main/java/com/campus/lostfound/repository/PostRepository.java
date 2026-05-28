package com.campus.lostfound.repository;

import com.campus.lostfound.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Post> findByModStatusOrderByCreatedAtAsc(String modStatus);

    List<Post> findByTypeAndModStatus(String type, String modStatus);

    long countByModStatus(String modStatus);

    long countByTypeAndModStatus(String type, String modStatus);

    long countByPostStatus(String postStatus);
}
