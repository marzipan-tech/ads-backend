package io.github.marzipan.ads.service;

import io.github.marzipan.ads.dto.request.NewPasswordRequestDto;
import io.github.marzipan.ads.dto.request.UpdateUserRequestDto;
import io.github.marzipan.ads.dto.response.UserResponseDto;
import io.github.marzipan.ads.entity.Role;
import io.github.marzipan.ads.entity.User;
import io.github.marzipan.ads.exception.BadRequestException;
import io.github.marzipan.ads.mapper.UserMapper;
import io.github.marzipan.ads.repository.UserRepository;
import io.github.marzipan.ads.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UserServiceImpl userService;

    private User currentUser;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        userResponseDto = new UserResponseDto(1, "email", "name", "surname", "phone", Role.USER, "image");
    }

    @Test
    void setPassword_shouldSetPassword() {
        currentUser.setPassword("password");
        NewPasswordRequestDto dto = new NewPasswordRequestDto();
        dto.setCurrentPassword("password");
        dto.setNewPassword("new_password");
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(passwordEncoder.encode("new_password")).thenReturn("encoded_password");

        userService.setPassword(dto);

        assertEquals("encoded_password", currentUser.getPassword());
        verify(currentUserService).getCurrentUser();
        verify(passwordEncoder).matches("password", "password");
        verify(passwordEncoder).encode("new_password");
    }

    @Test
    void setPassword_shouldThrowBadRequestException_whenIncorrectCurrentPassword() {
        currentUser.setPassword("password");
        NewPasswordRequestDto dto = new NewPasswordRequestDto();
        dto.setCurrentPassword("other_password");
        dto.setNewPassword("new_password");
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.matches("other_password", "password")).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.setPassword(dto));
        assertEquals("Incorrect current password", exception.getMessage());
        verify(currentUserService).getCurrentUser();
        verify(passwordEncoder).matches("other_password", "password");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUserResponseDto() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(userMapper.userToDto(currentUser)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getCurrentUser();

        assertEquals(userResponseDto, result);
        verify(currentUserService).getCurrentUser();
        verify(userMapper).userToDto(currentUser);
    }

    @Test
    void updateUser_shouldReturnUpdatedUserResponseDto() {
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto();
        updateUserRequestDto.setFirstName("name");
        updateUserRequestDto.setLastName("surname");
        updateUserRequestDto.setPhone("phone");

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.save(currentUser)).thenReturn(currentUser);
        when(userMapper.userToDto(currentUser)).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateUser(updateUserRequestDto);

        assertEquals(userResponseDto, result);
        assertEquals("name", currentUser.getFirstName());
        assertEquals("surname", currentUser.getLastName());
        assertEquals("phone", currentUser.getPhone());
        verify(currentUserService).getCurrentUser();
        verify(userRepository).save(currentUser);
        verify(userMapper).userToDto(currentUser);

    }

    @Test
    void updateUserImage_shouldUpdateUserImage() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        currentUser.setId(1);
        MultipartFile image = mock(MultipartFile.class);
        when(fileStorageService.saveUserImage(currentUser.getId(), image)).thenReturn("new_image.jpg");

        userService.updateUserImage(image);

        assertEquals("new_image.jpg", currentUser.getImage());
        verify(currentUserService).getCurrentUser();
        verify(fileStorageService).saveUserImage(currentUser.getId(), image);
        verify(userRepository).save(currentUser);
    }
}
