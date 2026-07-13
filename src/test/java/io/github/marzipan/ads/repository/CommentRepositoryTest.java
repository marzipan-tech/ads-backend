package io.github.marzipan.ads.repository;

import io.github.marzipan.ads.entity.Ad;
import io.github.marzipan.ads.entity.Comment;
import io.github.marzipan.ads.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AdRepository adRepository;
    @Autowired
    private UserRepository userRepository;

    private Ad ad;
    private Comment comment;

    @BeforeEach
    void setUp() {
        ad = new Ad();
        adRepository.save(ad);
        User user = new User();
        user.setUsername("Mary");
        userRepository.save(user);
        comment = new Comment();
        comment.setAd(ad);
        comment.setAuthor(user);
        commentRepository.save(comment);
    }

    @Test
    void findByAdId_shouldFindCommentByAdId() {
        List<Comment> comments = commentRepository.findByAdId(ad.getId());

        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
    }

    @Test
    void existsByIdAndAuthorUsername_shouldExist() {
        assertTrue(commentRepository.existsByIdAndAuthorUsername(comment.getId(), "Mary"));
    }

    @Test
    void existsByIdAndAuthorUsername_shouldNotExist() {
        assertFalse(commentRepository.existsByIdAndAuthorUsername(comment.getId(), "Jane"));
    }
}
