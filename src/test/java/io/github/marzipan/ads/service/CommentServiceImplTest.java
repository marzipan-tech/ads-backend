package io.github.marzipan.ads.service;

import io.github.marzipan.ads.dto.request.CommentRequestDto;
import io.github.marzipan.ads.dto.response.CommentResponseDto;
import io.github.marzipan.ads.dto.response.CommentsResponseDto;
import io.github.marzipan.ads.entity.Ad;
import io.github.marzipan.ads.entity.Comment;
import io.github.marzipan.ads.entity.User;
import io.github.marzipan.ads.exception.NotFoundException;
import io.github.marzipan.ads.mapper.CommentMapper;
import io.github.marzipan.ads.repository.AdRepository;
import io.github.marzipan.ads.repository.CommentRepository;
import io.github.marzipan.ads.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private AdRepository adRepository;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Ad ad;
    private Comment comment;
    private CommentResponseDto commentResponseDto;
    private CommentsResponseDto commentsResponseDto;
    private List<Comment> comments;
    private User currentUser;

    @BeforeEach
    void setUp() {
        ad = new Ad();
        comment = new Comment();
        commentResponseDto = new CommentResponseDto(1, "image", "name", 1L, 1, "text");
        commentsResponseDto = new CommentsResponseDto(1, List.of(commentResponseDto));
        comments = List.of(comment);
        currentUser = new User();
    }

    @Test
    void getComments_shouldReturnAllCommentsForSpecifiedAd() {
        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        when(commentRepository.findByAdId(1)).thenReturn(comments);
        when(commentMapper.toCommentsResponseDto(comments)).thenReturn(commentsResponseDto);

        CommentsResponseDto result = commentService.getComments(1);

        assertEquals(commentsResponseDto, result);
        verify(adRepository).findById(1);
        verify(commentRepository).findByAdId(1);
        verify(commentMapper).toCommentsResponseDto(comments);
    }

    @Test
    void getComments_shouldThrowNotFoundException_whenAdNotFound() {
        when(adRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getComments(1));
        verify(adRepository).findById(1);
        verify(commentRepository, never()).findByAdId(1);
        verify(commentMapper, never()).toCommentsResponseDto(any());
    }

    @Test
    void addComment_shouldReturnCreatedCommentResponseDto() {
        CommentRequestDto dto = new CommentRequestDto();
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        when(commentMapper.commentRequestDtoToComment(dto)).thenReturn(comment);
        when(commentMapper.commentToDto(comment)).thenReturn(commentResponseDto);

        CommentResponseDto result = commentService.addComment(1, dto);

        assertEquals(commentResponseDto, result);
        assertEquals(currentUser, comment.getAuthor());
        assertEquals(ad, comment.getAd());
        assertNotNull(comment.getCreatedAt());
        verify(currentUserService).getCurrentUser();
        verify(adRepository).findById(1);
        verify(commentRepository).save(comment);
        verify(commentMapper).commentToDto(comment);
    }

    @Test
    void addComment_shouldThrowNotFoundException_whenAdNotFound() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(adRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.addComment(1, new CommentRequestDto()));
        verify(currentUserService).getCurrentUser();
        verify(adRepository).findById(1);
        verify(commentMapper, never()).commentRequestDtoToComment(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_shouldReturnUpdatedCommentResponseDto() {
        ad.setId(1);
        comment.setAd(ad);
        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("some text");
        when(commentRepository.findById(2)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.commentToDto(comment)).thenReturn(commentResponseDto);

        CommentResponseDto result = commentService.updateComment(1, 2, dto);

        assertEquals(commentResponseDto, result);
        assertEquals("some text", comment.getText());
        verify(commentRepository).findById(2);
        verify(commentRepository).save(comment);
        verify(commentMapper).commentToDto(comment);
    }

    @Test
    void updateComment_shouldThrowNotFoundException_whenCommentDoesNotBelongToAd() {
        ad.setId(1);
        comment.setAd(ad);
        when(commentRepository.findById(2)).thenReturn(Optional.of(comment));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> commentService.updateComment(2, 2, new CommentRequestDto()));
        assertEquals("Comment doesn't belong to this ad", exception.getMessage());
        verify(commentRepository).findById(2);
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).commentToDto(any());
    }

    @Test
    void updateComment_shouldThrowNotFoundException_whenCommentNotFound() {
        when(commentRepository.findById(2)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> commentService.updateComment(1, 2, new CommentRequestDto()));
        assertEquals("Comment not found", exception.getMessage());
        verify(commentRepository).findById(2);
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).commentToDto(any());
    }

    @Test
    void deleteComment_shouldDeleteExistingComment() {
        comment.setAd(ad);
        ad.setId(1);
        when(commentRepository.findById(2)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1, 2);

        verify(commentRepository).delete(comment);
        verify(commentRepository).findById(2);
    }

    @Test
    void deleteComment_shouldThrowNotFoundException_whenCommentDoesNotBelongToAd() {
        ad.setId(1);
        comment.setAd(ad);
        when(commentRepository.findById(2)).thenReturn(Optional.of(comment));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> commentService.deleteComment(2, 2));
        assertEquals("Comment doesn't belong to this ad", exception.getMessage());
        verify(commentRepository).findById(2);
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_shouldThrowNotFoundException_whenCommentNotFound() {
        when(commentRepository.findById(2)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> commentService.deleteComment(1, 2));
        assertEquals("Comment not found", exception.getMessage());
        verify(commentRepository).findById(2);
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void isCommentOwner_shouldReturnTrue_whenUserIsCommentOwner() {
        when(authentication.getName()).thenReturn("name");
        when(commentRepository.existsByIdAndAuthorUsername(1, "name")).thenReturn(true);

        assertTrue(commentService.isCommentOwner(1, authentication));
        verify(authentication).getName();
        verify(commentRepository).existsByIdAndAuthorUsername(1, "name");
    }

    @Test
    void isCommentOwner_shouldReturnFalse_whenUserIsNotCommentOwner() {
        when(authentication.getName()).thenReturn("name");
        when(commentRepository.existsByIdAndAuthorUsername(1, "name")).thenReturn(false);

        assertFalse(commentService.isCommentOwner(1, authentication));
        verify(authentication).getName();
        verify(commentRepository).existsByIdAndAuthorUsername(1, "name");
    }
}
