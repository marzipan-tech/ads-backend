package io.github.marzipan.ads.repository;

import io.github.marzipan.ads.entity.Ad;
import io.github.marzipan.ads.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AdRepositoryTest {

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Ad ad;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("Mary");
        userRepository.save(user);
        ad = new Ad();
        ad.setAuthor(user);
        adRepository.save(ad);
    }

    @Test
    void findAllByAuthor_ShouldReturnUserAds() {
        List<Ad> ads = adRepository.findAllByAuthor(user);
        assertEquals(1, ads.size());
        assertEquals(ad.getId(), ads.get(0).getId());
    }

    @Test
    void existsByIdAndAuthorUsername_ShouldReturnTrue() {
        assertTrue(adRepository.existsByIdAndAuthorUsername(ad.getId(), "Mary"));
    }

    @Test
    void existsByIdAndAuthorUsername_ShouldNotReturnTrue() {
        assertFalse(adRepository.existsByIdAndAuthorUsername(ad.getId(), "Jane"));
    }
}
