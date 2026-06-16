package io.github.marzipan.ads.mapper;

import org.mapstruct.*;
import io.github.marzipan.ads.dto.response.UserResponseDto;
import io.github.marzipan.ads.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "username", target = "email")
    UserResponseDto userToDto(User user);
}
