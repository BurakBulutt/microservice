package com.example.servicemedia.domain.content.elasticsearch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Document(indexName = "content",aliases = {@Alias(alias = "content")})
@Document(indexName = "content")
@Mapping(mappingPath = "elastic/mappings/content.json")
@Setting(settingPath = "elastic/settings/fullTextSearchAnalyzerSettings.json")
public class ElasticContent {
    private String id;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime created;
    @Field(type = FieldType.Text, analyzer = "autocomplete_tr", searchAnalyzer = "autocomplete_tr_search")
    private String name;
    private String photoUrl;
    private String slug;
    @Field(type = FieldType.Keyword)
    private String type;
    @Field(type = FieldType.Keyword)
    private List<String> categories;
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate startDate;
}
