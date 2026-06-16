package io.github.marzipan.ads.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import io.github.marzipan.ads.dto.request.NewPasswordRequestDto;
import io.github.marzipan.ads.dto.request.UpdateUserRequestDto;
import io.github.marzipan.ads.dto.response.UserResponseDto;
import io.github.marzipan.ads.entity.User;
import io.github.marzipan.ads.exception.BadRequestException;
import io.github.marzipan.ads.mapper.UserMapper;
import io.github.marzipan.ads.repository.UserRepository;
import io.github.marzipan.ads.service.CurrentUserService;
import io.github.marzipan.ads.service.FileStorageService;
import io.github.marzipan.ads.service.UserService;

/**
 * Сервис управления пользователем.
 * Отвечает за:
 * - изменение пароля,
 * - обновление профиля,
 * - загрузку аватара.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final CurrentUserService currentUserService;

    /**
     * Изменение пароля пользователя.
     * @throws BadRequestException если текущий пароль неверный
     */
    @Override
    public void setPassword(NewPasswordRequestDto dto) {
        User user = currentUserService.getCurrentUser();
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect current password");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser() {
        User user = currentUserService.getCurrentUser();
        return userMapper.userToDto(user);
    }

    /**
     * Обновление данных профиля пользователя.
     */
    @Override
    public UserResponseDto updateUser(UpdateUserRequestDto dto) {
        User user = currentUserService.getCurrentUser();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        User updatedUser = userRepository.save(user);
        return userMapper.userToDto(updatedUser);
    }

    /**
     * Обновление аватара пользователя.
     */
    @Override
    public void updateUserImage(MultipartFile image) {
        User user = currentUserService.getCurrentUser();
        String imagePath = fileStorageService.saveUserImage(user.getId(), image);
        user.setImage(imagePath);
        userRepository.save(user);
    }
}
