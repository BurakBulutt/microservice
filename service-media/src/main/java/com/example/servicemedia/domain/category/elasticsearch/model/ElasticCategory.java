package com.example.servicemedia.domain.category.elasticsearch.model;

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
@Document(indexName = "category")
//@Document(indexName = "category",aliases = {@Alias(alias = "category")})
@Mapping(mappingPath = "elastic/mappings/category.json")
@Setting(settingPath = "elastic/settings/fullTextSearchAnalyzerSettings.json")
public class ElasticCategory {
    private String id;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime created;
    @Field(type = FieldType.Text, analyzer = "autocomplete_tr", searchAnalyzer = "autocomplete_tr_search")
    private String name;
    @Field(type = FieldType.Keyword)
    private String slug;
}
