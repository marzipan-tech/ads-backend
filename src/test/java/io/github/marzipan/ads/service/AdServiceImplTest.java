package io.github.marzipan.ads.service;

import io.github.marzipan.ads.dto.request.AdRequestDto;
import io.github.marzipan.ads.dto.response.AdResponseDto;
import io.github.marzipan.ads.dto.response.AdsResponseDto;
import io.github.marzipan.ads.dto.response.ExtendedAdResponseDto;
import io.github.marzipan.ads.entity.Ad;
import io.github.marzipan.ads.entity.User;
import io.github.marzipan.ads.exception.NotFoundException;
import io.github.marzipan.ads.mapper.AdMapper;
import io.github.marzipan.ads.repository.AdRepository;
import io.github.marzipan.ads.service.impl.AdServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdServiceImplTest {
    @Mock
    private AdRepository adRepository;
    @Mock
    private AdMapper adMapper;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private MultipartFile image;
    @InjectMocks
    private AdServiceImpl adService;

    private Ad ad;
    private List<Ad> ads;
    private AdResponseDto adResponseDto;
    private AdsResponseDto adsResponseDto;
    private User currentUser;
    private AdRequestDto adRequestDto;

    @BeforeEach
    void setUp() {
        ad = new Ad();
        ads = List.of(ad);
        adResponseDto = new AdResponseDto(1, "image", 1, 100, "title");
        adsResponseDto = new AdsResponseDto(1, List.of(adResponseDto));
        currentUser = new User();
        adRequestDto = new AdRequestDto();
    }

    @Test
    void getAllAds_shouldReturnAdsResponseDto() {
        when(adRepository.findAll()).thenReturn(ads);
        when(adMapper.toAdsResponseDto(ads)).thenReturn(adsResponseDto);

        AdsResponseDto result = adService.getAllAds();

        assertEquals(adsResponseDto, result);
        verify(adRepository).findAll();
        verify(adMapper).toAdsResponseDto(ads);
    }

    @Test
    void getAd_shouldReturnAdWithGivenId() {
        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        ExtendedAdResponseDto extendedAdResponseDto = new ExtendedAdResponseDto(1, "name", "surname", "description", "email", "image", "phone", 100, "title");
        when(adMapper.extendedAdToDto(ad)).thenReturn(extendedAdResponseDto);

        ExtendedAdResponseDto result = adService.getAd(1);

        assertEquals(extendedAdResponseDto, result);
        verify(adRepository).findById(1);
        verify(adMapper).extendedAdToDto(ad);
    }

    @Test
    void getAd_shouldThrowNotFoundException_whenAdIsNotFound() {
        when(adRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adService.getAd(2));
        verify(adRepository).findById(2);
        verify(adMapper, never()).extendedAdToDto(any());
    }

    @Test
    void getMyAds_shouldReturnUserAds() {
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(adRepository.findAllByAuthor(currentUser)).thenReturn(ads);
        when(adMapper.toAdsResponseDto(ads)).thenReturn(adsResponseDto);

        AdsResponseDto result = adService.getMyAds();

        assertEquals(adsResponseDto, result);
        verify(currentUserService).getCurrentUser();
        verify(adRepository).findAllByAuthor(currentUser);
        verify(adMapper).toAdsResponseDto(ads);
    }

    @Test
    void addAd_shouldSaveNewAdAndReturnResponseDto() {
        ad.setId(1);
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(adMapper.adRequestDtoToAd(adRequestDto)).thenReturn(ad);
        when(fileStorageService.saveAdImage(ad.getId(), image)).thenReturn("image.jpg");
        when(adMapper.adToDto(ad)).thenReturn(adResponseDto);

        AdResponseDto result = adService.addAd(adRequestDto, image);

        assertEquals(adResponseDto, result);
        assertEquals("image.jpg", ad.getImage());
        assertEquals(currentUser, ad.getAuthor());
        verify(currentUserService).getCurrentUser();
        verify(adMapper).adRequestDtoToAd(adRequestDto);
        verify(adRepository).save(ad);
        verify(fileStorageService).saveAdImage(ad.getId(), image);
        verify(adMapper).adToDto(ad);
    }

    @Test
    void updateAd_shouldReturnUpdatedAdResponseDto() {
        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        when(adMapper.adToDto(ad)).thenReturn(adResponseDto);

        AdResponseDto result = adService.updateAd(1, adRequestDto);

        assertEquals(adResponseDto, result);
        verify(adRepository).findById(1);
        verify(adMapper).updateAdFromDto(adRequestDto, ad);
        verify(adRepository).save(ad);
        verify(adMapper).adToDto(ad);
    }

    @Test
    void updateAd_shouldThrowNotFoundException_whenAdIsNotFound() {
        when(adRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adService.updateAd(2, adRequestDto));
        verify(adRepository).findById(2);
        verify(adMapper, never()).updateAdFromDto(any(), any());
        verify(adRepository, never()).save(any());
    }

    @Test
    void deleteAd_shouldDeleteSpecifiedAd() {
        when(adRepository.findById(1)).thenReturn(Optional.of(ad));

        adService.deleteAd(1);

        verify(adRepository).findById(1);
        verify(adRepository).delete(ad);
    }

    @Test
    void deleteAd_shouldThrowNotFoundException_whenAdIsNotFound() {
        when(adRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adService.deleteAd(2));
        verify(adRepository).findById(2);
        verify(adRepository, never()).delete(any());
    }

    @Test
    void updateImage_shouldSaveUpdatedAd() {
        ad.setId(1);
        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        when(fileStorageService.saveAdImage(ad.getId(), image)).thenReturn("image.jpg");

        adService.updateImage(ad.getId(), image);

        verify(adRepository).findById(1);
        verify(fileStorageService).saveAdImage(ad.getId(), image);
        verify(adRepository).save(ad);
    }

    @Test
    void updateImage_shouldThrowNotFoundException_whenAdIsNotFound() {
        when(adRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adService.updateImage(2, image));
        verify(adRepository).findById(2);
        verify(fileStorageService, never()).saveAdImage(anyInt(), any());
        verify(adRepository, never()).save(any());
    }
}
