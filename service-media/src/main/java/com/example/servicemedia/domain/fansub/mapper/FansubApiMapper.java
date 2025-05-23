package com.example.servicemedia.domain.fansub.mapper;

import com.example.servicemedia.domain.fansub.api.FansubRequest;
import com.example.servicemedia.domain.fansub.api.FansubResponse;
import com.example.servicemedia.domain.fansub.dto.FansubDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FansubApiMapper {

    public static FansubResponse toResponse(FansubDto dto){
        return FansubResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .url(dto.getUrl())
                .build();
    }

    public static FansubDto toDto(FansubRequest request){
        return FansubDto.builder()
                .name(request.name().trim())
                .url(request.url())
                .build();
    }

    public static Page<FansubResponse> toPageResponse(Page<FansubDto> dtoPage){
        return dtoPage.map(FansubApiMapper::toResponse);
    }
}
