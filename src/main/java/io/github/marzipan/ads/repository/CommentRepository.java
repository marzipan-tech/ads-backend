package io.github.marzipan.ads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.marzipan.ads.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByAdId(Integer adId);

    boolean existsByIdAndAuthorUsername(Integer commentId, String name);
}
