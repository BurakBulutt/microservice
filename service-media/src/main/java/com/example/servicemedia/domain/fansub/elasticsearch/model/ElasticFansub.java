package com.example.servicemedia.domain.fansub.elasticsearch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.OffsetDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Document(indexName = "fansub",aliases = {@Alias(alias = "fansub")})
@Document(indexName = "fansub")
@Mapping(mappingPath = "elastic/mappings/fansub.json")
@Setting(settingPath = "elastic/settings/fullTextSearchAnalyzerSettings.json")
public class ElasticFansub {
    private String id;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime created;
    @Field(type = FieldType.Text, analyzer = "autocomplete_tr", searchAnalyzer = "autocomplete_tr_search")
    private String name;
}
