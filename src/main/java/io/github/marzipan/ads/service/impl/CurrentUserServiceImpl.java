package io.github.marzipan.ads.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import io.github.marzipan.ads.entity.User;
import io.github.marzipan.ads.exception.NotFoundException;
import io.github.marzipan.ads.repository.UserRepository;
import io.github.marzipan.ads.service.CurrentUserService;

/**
 * Сервис получения текущего авторизованного пользователя.
 * Извлекает пользователя из SecurityContext.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {
    private final UserRepository userRepository;

    /**
     * Получение текущего пользователя из SecurityContext.
     * @return текущий User
     * @throws NotFoundException если пользователь не аутентифицирован
     */
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new NotFoundException("User not authenticated");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
