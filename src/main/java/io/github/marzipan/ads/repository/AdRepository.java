package io.github.marzipan.ads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.marzipan.ads.entity.Ad;
import io.github.marzipan.ads.entity.User;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Integer> {
    List<Ad> findAllByAuthor(User author);

    boolean existsByIdAndAuthorUsername(Integer adId, String name);
}
