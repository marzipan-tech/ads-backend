package io.github.marzipan.ads.controller;

import io.github.marzipan.ads.dto.request.RegisterRequestDto;
import io.github.marzipan.ads.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void login_shouldReturnOk() throws Exception {
        String json = "{"
                + "\"username\": \"username\","
                + "\"password\": \"password\""
                + "}";
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk());
        verify(authService).login(anyString(), anyString());
    }

    @Test
    void register_shouldReturnCreated() throws Exception {
        String json = "{"
                + "\"username\": \"username\","
                + "\"password\": \"password\","
                + "\"firstName\": \"name\","
                + "\"lastName\": \"surname\","
                + "\"phone\": \"phone\","
                + "\"role\": \"USER\""
                + "}";
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isCreated());
        verify(authService).register(any(RegisterRequestDto.class));
    }
}
