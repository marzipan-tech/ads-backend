package io.github.marzipan.ads.service;

import org.springframework.web.multipart.MultipartFile;
import io.github.marzipan.ads.dto.request.NewPasswordRequestDto;
import io.github.marzipan.ads.dto.request.UpdateUserRequestDto;
import io.github.marzipan.ads.dto.response.UserResponseDto;

public interface UserService {
    void setPassword(NewPasswordRequestDto dto);

    UserResponseDto getCurrentUser();

    UserResponseDto updateUser(UpdateUserRequestDto dto);

    void updateUserImage(MultipartFile image);
}
