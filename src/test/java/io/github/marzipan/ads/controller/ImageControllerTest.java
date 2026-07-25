package io.github.marzipan.ads.controller;

import io.github.marzipan.ads.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    @WithMockUser
    void getImage_shouldReturn200AndImage() throws Exception {
        byte[] image = "test".getBytes();

        when(fileStorageService.getImage("/test.jpg")).thenReturn(image);

        mockMvc.perform(get("/images/test.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(image));
        verify(fileStorageService).getImage("/test.jpg");
    }
}
