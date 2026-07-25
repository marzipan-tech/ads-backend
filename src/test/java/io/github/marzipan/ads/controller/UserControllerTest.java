package io.github.marzipan.ads.controller;

import io.github.marzipan.ads.dto.request.NewPasswordRequestDto;
import io.github.marzipan.ads.dto.request.UpdateUserRequestDto;
import io.github.marzipan.ads.dto.response.UserResponseDto;
import io.github.marzipan.ads.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static io.github.marzipan.ads.entity.Role.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserResponseDto userResponseDto;
    private String json;

    @BeforeEach
    void setUp() {
        json = "{"
                + "\"currentPassword\": \"password\","
                + "\"newPassword\": \"newpassword\""
                + "}";
        userResponseDto = new UserResponseDto(1, "email", "name", "surname", "phone", USER, "image");
    }

    @Test
    @WithMockUser
    void setPassword_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk());
        verify(userService).setPassword(any(NewPasswordRequestDto.class));
    }

    @Test
    @WithMockUser
    void getUser_shouldReturn200AndCurrentUser() throws Exception {
        when(userService.getCurrentUser()).thenReturn(userResponseDto);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.firstName").value("name"))
                .andExpect(jsonPath("$.lastName").value("surname"))
                .andExpect(jsonPath("$.phone").value("phone"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.image").value("image"));
        verify(userService).getCurrentUser();
    }

    @Test
    @WithMockUser
    void updateUser_shouldReturn200AndUpdatedUser() throws Exception {
        when(userService.updateUser(any(UpdateUserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.firstName").value("name"))
                .andExpect(jsonPath("$.lastName").value("surname"))
                .andExpect(jsonPath("$.phone").value("phone"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.image").value("image"));
        verify(userService).updateUser(any(UpdateUserRequestDto.class));
    }

    @Test
    @WithMockUser
    void updateUserImage_shouldReturnOk() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpg",
                "test image".getBytes()
        );
        mockMvc.perform(multipart("/users/me/image")
                        .file(image)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk());
        verify(userService).updateUserImage(any(MultipartFile.class));
    }
}
