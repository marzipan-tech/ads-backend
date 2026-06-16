package io.github.marzipan.ads.dto.response;

import lombok.Value;

@Value
public class ExtendedAdResponseDto {
    Integer pk;
    String authorFirstName;
    String authorLastName;
    String description;
    String email;
    String image;
    String phone;
    Integer price;
    String title;
}
