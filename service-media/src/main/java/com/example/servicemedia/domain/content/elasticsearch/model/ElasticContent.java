package com.example.servicemedia.domain.content.elasticsearch.model;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(indexName = ElasticContent.INDEX_NAME,aliases = {@Alias(alias = "content")})
@Mapping(mappingPath = "elastic/mappings/content.json")
@Setting(settingPath = "elastic/settings/fullTextSearchAnalyzerSettings.json")
public class ElasticContent {
    public static final String INDEX_NAME = "content_v1";

    private String id;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime created;
    @Field(type = FieldType.Text, analyzer = "autocomplete_tr", searchAnalyzer = "autocomplete_tr_search")
    private String name;
    @Field(type = FieldType.Keyword)
    private String photoUrl;
    @Field(type = FieldType.Keyword)
    private String slug;
    @Field(type = FieldType.Keyword)
    private String type;
    @Field(type = FieldType.Keyword)
    private List<String> categories;
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate startDate;
}
