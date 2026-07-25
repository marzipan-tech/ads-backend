package io.github.marzipan.ads.controller;

import io.github.marzipan.ads.dto.request.CommentRequestDto;
import io.github.marzipan.ads.dto.response.CommentResponseDto;
import io.github.marzipan.ads.dto.response.CommentsResponseDto;
import io.github.marzipan.ads.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private CommentResponseDto commentResponseDto;
    private String json;

    @BeforeEach
    void setUp() {
        json = "{"
                + "\"text\": \"some text\""
                + "}";
        commentResponseDto = new CommentResponseDto(1, "image", "name", 2L, 1, "some text");
    }

    @Test
    @WithMockUser
    void getComments_shouldReturnOkAndComments() throws Exception {
        CommentsResponseDto commentsResponseDto = new CommentsResponseDto(1, List.of(commentResponseDto));

        when(commentService.getComments(1)).thenReturn(commentsResponseDto);

        mockMvc.perform(get("/ads/{id}/comments", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].author").value(1))
                .andExpect(jsonPath("$.results[0].authorImage").value("image"))
                .andExpect(jsonPath("$.results[0].authorFirstName").value("name"))
                .andExpect(jsonPath("$.results[0].createdAt").value(2L))
                .andExpect(jsonPath("$.results[0].pk").value(1))
                .andExpect(jsonPath("$.results[0].text").value("some text"));
        verify(commentService).getComments(1);
    }

    @Test
    @WithMockUser
    void addComment_shouldReturnOkAndAddedComment() throws Exception {
        when(commentService.addComment(eq(1), any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(post("/ads/{id}/comments", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(1))
                .andExpect(jsonPath("$.authorImage").value("image"))
                .andExpect(jsonPath("$.authorFirstName").value("name"))
                .andExpect(jsonPath("$.createdAt").value(2L))
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.text").value("some text"));
        verify(commentService).addComment(eq(1), any(CommentRequestDto.class));
    }

    @Test
    @WithMockUser
    void updateComment_shouldReturnOkAndUpdatedComment() throws Exception {
        when(commentService.updateComment(eq(1), eq(1), any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(1))
                .andExpect(jsonPath("$.authorImage").value("image"))
                .andExpect(jsonPath("$.authorFirstName").value("name"))
                .andExpect(jsonPath("$.createdAt").value(2L))
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.text").value("some text"));
        verify(commentService).updateComment(eq(1), eq(1), any(CommentRequestDto.class));
    }

    @Test
    @WithMockUser
    void deleteComment_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", 1, 1)
                        .with(csrf()))
                .andExpect(status().isOk());
        verify(commentService).deleteComment(1, 1);
    }
}
