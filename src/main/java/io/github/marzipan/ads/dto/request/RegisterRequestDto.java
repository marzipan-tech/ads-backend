package io.github.marzipan.ads.dto.request;

import lombok.Data;
import io.github.marzipan.ads.entity.Role;

@Data
public class RegisterRequestDto {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
}
