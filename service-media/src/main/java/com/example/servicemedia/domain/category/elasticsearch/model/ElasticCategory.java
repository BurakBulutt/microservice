package com.example.servicemedia.domain.category.elasticsearch.model;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.OffsetDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(indexName = ElasticCategory.INDEX_NAME, aliases = {@Alias(alias = "category")})
@Mapping(mappingPath = "elastic/mappings/category.json")
@Setting(settingPath = "elastic/settings/fullTextSearchAnalyzerSettings.json")
public class ElasticCategory {
    public static final String INDEX_NAME = "category_v1";

    private String id;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime created;
    @Field(type = FieldType.Text, analyzer = "autocomplete_tr", searchAnalyzer = "autocomplete_tr_search")
    private String name;
    @Field(type = FieldType.Keyword)
    private String slug;
}
