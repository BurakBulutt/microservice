package com.example.servicemedia.content.api;

import com.example.servicemedia.content.enums.ContentType;
import com.example.servicemedia.media.dto.MediaDto;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class ContentResponse {
    private String id;
    private String name;
    private String description;
    private String photoUrl;
    private ContentType type;
    private String subject;
    private Date startDate;
    private String slug;
    private List<MediaDto> mediaList;
}
