package io.github.marzipan.ads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.marzipan.ads.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
