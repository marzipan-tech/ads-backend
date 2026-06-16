package io.github.marzipan.ads.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import io.github.marzipan.ads.dto.response.CommentResponseDto;
import io.github.marzipan.ads.dto.response.CommentsResponseDto;
import io.github.marzipan.ads.dto.request.CommentRequestDto;
import io.github.marzipan.ads.entity.Comment;
import io.github.marzipan.ads.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.image", target = "authorImage")
    CommentResponseDto commentToDto(Comment comment);

    List<CommentResponseDto> commentsToDtoList(List<Comment> comments);

    default CommentsResponseDto toCommentsResponseDto(List<Comment> comments) {
        return new CommentsResponseDto(comments.size(), commentsToDtoList(comments));
    }

    default Integer map(User user) {
        return user != null ? user.getId() : null;
    }

    Comment commentRequestDtoToComment(CommentRequestDto dto);
}
