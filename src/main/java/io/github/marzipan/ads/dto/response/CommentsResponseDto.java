package io.github.marzipan.ads.dto.response;

import lombok.Value;

import java.util.List;

@Value
public class CommentsResponseDto {
    Integer count;
    List<CommentResponseDto> results;
}
