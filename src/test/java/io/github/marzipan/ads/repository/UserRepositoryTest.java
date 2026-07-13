package io.github.marzipan.ads.repository;

import io.github.marzipan.ads.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("Mary");
        userRepository.save(user);
    }

    @Test
    void findByUsername_shouldFindUserByUsername() {
        assertTrue(userRepository.findByUsername("Mary").isPresent());
    }

    @Test
    void existsByUsername_shouldExistByUsername() {
        assertTrue(userRepository.existsByUsername("Mary"));
    }

    @Test
    void existsByUsername_shouldNotExistByUsername() {
        assertFalse(userRepository.existsByUsername("Kate"));
    }
}
