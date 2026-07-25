package io.github.marzipan.ads.service;

import io.github.marzipan.ads.dto.request.RegisterRequestDto;
import io.github.marzipan.ads.entity.Role;
import io.github.marzipan.ads.entity.User;
import io.github.marzipan.ads.exception.BadRequestException;
import io.github.marzipan.ads.repository.UserRepository;
import io.github.marzipan.ads.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_shouldAuthenticateAndSetSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        authService.login("user", "password");

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        Authentication token = captor.getValue();
        assertEquals("user", token.getPrincipal());
        assertEquals("password", token.getCredentials());
        Authentication actual = SecurityContextHolder.getContext().getAuthentication();
        assertSame(authentication, actual);
    }

    @Test
    void register_shouldSaveNewUser() {
        RegisterRequestDto registerRequestDto = createDto();
        when(userRepository.existsByUsername(registerRequestDto.getUsername())).thenReturn(false);
        when(encoder.encode(registerRequestDto.getPassword())).thenReturn("encodedPassword");

        authService.register(registerRequestDto);

        verify(userRepository).existsByUsername("user");
        verify(encoder).encode("password");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals("user", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("name", savedUser.getFirstName());
        assertEquals("surname", savedUser.getLastName());
        assertEquals("phone", savedUser.getPhone());
        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    void register_shouldThrowBadRequestException_whenUserAlreadyExists() {
        RegisterRequestDto registerRequestDto = createDto();
        when(userRepository.existsByUsername(registerRequestDto.getUsername())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(registerRequestDto));
        verify(encoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    private RegisterRequestDto createDto() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername("user");
        registerRequestDto.setPassword("password");
        registerRequestDto.setFirstName("name");
        registerRequestDto.setLastName("surname");
        registerRequestDto.setPhone("phone");
        registerRequestDto.setRole(Role.USER);
        return registerRequestDto;
    }
}
