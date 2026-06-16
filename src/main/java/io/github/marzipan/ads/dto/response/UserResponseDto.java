package io.github.marzipan.ads.dto.response;

import lombok.Value;
import io.github.marzipan.ads.entity.Role;

@Value
public class UserResponseDto {
    Integer id;
    String email;
    String firstName;
    String lastName;
    String phone;
    Role role;
    String image;
}
