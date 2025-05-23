package com.example.servicemedia.domain.fansub.mapper;

import com.example.servicemedia.domain.fansub.dto.FansubDto;
import com.example.servicemedia.domain.fansub.model.Fansub;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FansubServiceMapper {

    public static FansubDto toDto(Fansub fanSub) {
        return FansubDto.builder()
                .id(fanSub.getId())
                .name(fanSub.getName())
                .url(fanSub.getUrl())
                .build();
    }

    public static Fansub toEntity(Fansub fanSub, FansubDto dto) {
        fanSub.setName(dto.getName());
        fanSub.setUrl(dto.getUrl());

        return fanSub;
    }
 }
