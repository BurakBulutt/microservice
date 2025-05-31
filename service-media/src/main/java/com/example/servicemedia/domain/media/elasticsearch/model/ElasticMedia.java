package com.example.servicemedia.domain.media.elasticsearch.model;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(indexName = ElasticMedia.INDEX_NAME,aliases = {@Alias(alias = "media")})
@Mapping(mappingPath = "elastic/mappings/media.json")
@Setting(settingPath = "elastic/settings/fullTextSearchAnalyzerSettings.json")
public class ElasticMedia {
    public static final String INDEX_NAME = "media_v1";

    private String id;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime created;
    @Field(type = FieldType.Text, analyzer = "autocomplete_tr", searchAnalyzer = "autocomplete_tr_search")
    private String name;
    @Field(type = FieldType.Integer)
    private Integer count;
    @Field(type = FieldType.Keyword)
    private String slug;
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate publishDate;
    @Field(type = FieldType.Keyword)
    private String contentId;
}
