package io.github.marzipan.ads.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import io.github.marzipan.ads.dto.response.AdResponseDto;
import io.github.marzipan.ads.dto.response.AdsResponseDto;
import io.github.marzipan.ads.dto.request.AdRequestDto;
import io.github.marzipan.ads.dto.response.ExtendedAdResponseDto;
import io.github.marzipan.ads.entity.Ad;
import io.github.marzipan.ads.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdMapper {
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    AdResponseDto adToDto(Ad ad);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "author.username", target = "email")
    @Mapping(source = "author.phone", target = "phone")
    ExtendedAdResponseDto extendedAdToDto(Ad ad);

    List<AdResponseDto> adsToDtoList(List<Ad> ads);

    default AdsResponseDto toAdsResponseDto(List<Ad> ads) {
        return new AdsResponseDto(ads.size(), adsToDtoList(ads));
    }

    Ad adRequestDtoToAd(AdRequestDto dto);

    void updateAdFromDto(AdRequestDto dto, @MappingTarget Ad ad);

    default Integer map(User user) {
        return user != null ? user.getId() : null;
    }
}
