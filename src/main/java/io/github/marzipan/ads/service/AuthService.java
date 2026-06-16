package io.github.marzipan.ads.service;

import io.github.marzipan.ads.dto.request.RegisterRequestDto;

public interface AuthService {
    void login(String username, String password);

    void register(RegisterRequestDto register);
}
