package com.example.servicemedia.media.api;

import com.example.servicemedia.media.dto.MediaSourceDto;

import java.time.LocalDate;
import java.util.List;


public record MediaRequest(
        String description,
        String contentId,
        Integer count,
        List<MediaSourceDto> mediaSourceList,
        LocalDate publishDate
) {
}
