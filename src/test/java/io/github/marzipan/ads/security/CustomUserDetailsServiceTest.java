package io.github.marzipan.ads.security;

import io.github.marzipan.ads.entity.Role;
import io.github.marzipan.ads.entity.User;
import io.github.marzipan.ads.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class CustomUserDetailsServiceTest {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setRole(Role.USER);
        userRepository.save(user);

        UserDetails details = customUserDetailsService.loadUserByUsername("username");

        assertEquals("username", details.getUsername());
        assertEquals("password", details.getPassword());
        assertEquals("ROLE_USER", details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionWhenUserIsNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("unknown"));
    }
}
