package com.example.servicemedia.domain.media.elasticsearch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Document(indexName = "media",aliases = {@Alias(alias = "media")})
@Document(indexName = "media")
@Mapping(mappingPath = "elastic/mappings/media.json")
@Setting(settingPath = "elastic/settings/fullTextSearchAnalyzerSettings.json")
public class ElasticMedia {
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
