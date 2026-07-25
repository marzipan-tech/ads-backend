package io.github.marzipan.ads.controller;

import io.github.marzipan.ads.dto.request.AdRequestDto;
import io.github.marzipan.ads.dto.response.AdResponseDto;
import io.github.marzipan.ads.dto.response.AdsResponseDto;
import io.github.marzipan.ads.dto.response.ExtendedAdResponseDto;
import io.github.marzipan.ads.service.AdService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdController.class)
public class AdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdService adService;

    private AdsResponseDto adsResponseDto;
    private AdResponseDto adResponseDto;
    private String json;
    private MockMultipartFile image;

    @BeforeEach
    void setUp() {
        adResponseDto = new AdResponseDto(1, "image", 1, 100, "title");
        adsResponseDto = new AdsResponseDto(1, List.of(adResponseDto));
        json = "{"
                + "\"title\": \"title\","
                + "\"price\": 100,"
                + "\"description\": \"description\""
                + "}";
        image = new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpg",
                "test image".getBytes()
        );
    }

    @Test
    @WithMockUser
    void getAllAds_shouldReturn200AndAllAds() throws Exception {
        when(adService.getAllAds()).thenReturn(adsResponseDto);

        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].author").value(1))
                .andExpect(jsonPath("$.results[0].image").value("image"))
                .andExpect(jsonPath("$.results[0].pk").value(1))
                .andExpect(jsonPath("$.results[0].price").value(100))
                .andExpect(jsonPath("$.results[0].title").value("title"));
        verify(adService).getAllAds();
    }

    @Test
    @WithMockUser
    void getAd_shouldReturn200AndAdWithGivenId() throws Exception {
        ExtendedAdResponseDto extendedAdResponseDto = new ExtendedAdResponseDto(1, "name", "surname", "description", "email", "image", "phone", 100, "title");

        when(adService.getAd(1)).thenReturn(extendedAdResponseDto);

        mockMvc.perform(get("/ads/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.authorFirstName").value("name"))
                .andExpect(jsonPath("$.authorLastName").value("surname"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.image").value("image"))
                .andExpect(jsonPath("$.phone").value("phone"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.title").value("title"));
        verify(adService).getAd(1);
    }

    @Test
    @WithMockUser
    void getMyAds_shouldReturn200AndAllUserAds() throws Exception {
        when(adService.getMyAds()).thenReturn(adsResponseDto);

        mockMvc.perform(get("/ads/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].author").value(1))
                .andExpect(jsonPath("$.results[0].image").value("image"))
                .andExpect(jsonPath("$.results[0].pk").value(1))
                .andExpect(jsonPath("$.results[0].price").value(100))
                .andExpect(jsonPath("$.results[0].title").value("title"));
        verify(adService).getMyAds();
    }

    @Test
    @WithMockUser
    void addAd_shouldReturn200AndCreatedAd() throws Exception {
        MockMultipartFile properties = new MockMultipartFile(
                "properties",
                "",
                "application/json",
                json.getBytes()
        );

        when(adService.addAd(any(AdRequestDto.class), any(MultipartFile.class))).thenReturn(adResponseDto);

        mockMvc.perform(multipart("/ads")
                        .file(properties)
                        .file(image)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value(1))
                .andExpect(jsonPath("$.image").value("image"))
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.title").value("title"));
        verify(adService).addAd(any(AdRequestDto.class), any(MultipartFile.class));
    }

    @Test
    @WithMockUser
    void updateAd_shouldReturn200AndUpdatedAd() throws Exception {
        when(adService.updateAd(eq(1), any(AdRequestDto.class))).thenReturn(adResponseDto);

        mockMvc.perform(patch("/ads/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(1))
                .andExpect(jsonPath("$.image").value("image"))
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.title").value("title"));
        verify(adService).updateAd(eq(1), any(AdRequestDto.class));
    }

    @Test
    @WithMockUser
    void updateImage_shouldReturn200() throws Exception {
        mockMvc.perform(multipart("/ads/{id}/image", 1)
                        .file(image)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk());
        verify(adService).updateImage(eq(1), any(MultipartFile.class));
    }

    @Test
    @WithMockUser
    void deleteAd_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/ads/{id}", 1)
                        .with(csrf()))
                .andExpect(status().isNoContent());
        verify(adService).deleteAd(1);
    }
}
