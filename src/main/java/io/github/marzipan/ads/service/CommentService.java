package io.github.marzipan.ads.service;

import org.springframework.security.core.Authentication;
import io.github.marzipan.ads.dto.request.CommentRequestDto;
import io.github.marzipan.ads.dto.response.CommentResponseDto;
import io.github.marzipan.ads.dto.response.CommentsResponseDto;

public interface CommentService {
    CommentsResponseDto getComments(Integer adId);

    CommentResponseDto addComment(Integer adId, CommentRequestDto dto);

    CommentResponseDto updateComment(Integer adId, Integer commentId, CommentRequestDto dto);

    void deleteComment(Integer adId, Integer commentId);

    boolean isCommentOwner(Integer commentId, Authentication authentication);
}
