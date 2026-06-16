package io.github.marzipan.ads.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.marzipan.ads.dto.request.CommentRequestDto;
import io.github.marzipan.ads.dto.response.CommentResponseDto;
import io.github.marzipan.ads.dto.response.CommentsResponseDto;
import io.github.marzipan.ads.service.CommentService;

import javax.validation.Valid;

/**
 * REST-контроллер для работы с комментариями к объявлениям.
 * Поддерживает:
 * - получение комментариев,
 * - добавление комментариев,
 * - редактирование,
 * - удаление.
 * Бизнес-логика реализована в {@link CommentService}.
 */
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    /**
     * Получение комментариев к объявлению.
     * @param adId идентификатор объявления
     * @return список комментариев
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<CommentsResponseDto> getComments(@PathVariable("id") Integer adId) {
        return ResponseEntity.ok(commentService.getComments(adId));
    }

    /**
     * Добавление комментария к объявлению.
     * @param adId идентификатор объявления
     * @param dto текст комментария
     * @return созданный комментарий
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable("id") Integer adId, @RequestBody @Valid CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.addComment(adId, dto));
    }

    /**
     * Обновление комментария.
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param dto новый текст комментария
     * @return обновлённый комментарий
     */
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Integer adId, @PathVariable Integer commentId, @RequestBody @Valid CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.updateComment(adId, commentId, dto));
    }

    /**
     * Удаление комментария.
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     */
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer adId, @PathVariable Integer commentId) {
        commentService.deleteComment(adId, commentId);
        return ResponseEntity.ok().build();
    }
}
