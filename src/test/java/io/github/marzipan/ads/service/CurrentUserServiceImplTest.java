package io.github.marzipan.ads.service;

import io.github.marzipan.ads.entity.User;
import io.github.marzipan.ads.exception.NotFoundException;
import io.github.marzipan.ads.repository.UserRepository;
import io.github.marzipan.ads.service.impl.CurrentUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrentUserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserServiceImpl currentUserService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUser() {
        User user = new User();
        user.setUsername("user");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        User currentUser = currentUserService.getCurrentUser();

        assertSame(user, currentUser);
        verify(userRepository).findByUsername("user");
    }

    @Test
    void getCurrentUser_shouldThrowNotFoundException_whenUserIsNotAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(null);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> currentUserService.getCurrentUser());
        assertEquals("User not authenticated", exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void getCurrentUser_shouldThrowNotFoundException_whenUserIsNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> currentUserService.getCurrentUser());
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByUsername("user");
    }
}
