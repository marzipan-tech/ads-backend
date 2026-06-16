package io.github.marzipan.ads.dto.response;

import lombok.Value;

@Value
public class AdResponseDto {
    Integer author;
    String image;
    Integer pk;
    Integer price;
    String title;
}
