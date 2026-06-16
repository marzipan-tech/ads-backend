package io.github.marzipan.ads.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import io.github.marzipan.ads.dto.request.AdRequestDto;
import io.github.marzipan.ads.dto.response.AdResponseDto;
import io.github.marzipan.ads.dto.response.AdsResponseDto;
import io.github.marzipan.ads.dto.response.ExtendedAdResponseDto;

public interface AdService {

    AdsResponseDto getAllAds();

    ExtendedAdResponseDto getAd(Integer id);

    AdsResponseDto getMyAds();

    AdResponseDto addAd(AdRequestDto properties, MultipartFile image);

    AdResponseDto updateAd(Integer id, AdRequestDto dto);

    void deleteAd(Integer id);

    void updateImage(Integer id, MultipartFile image);

    boolean isOwner(Integer adId, Authentication authentication);
}
