package com.example.servicemedia.domain.fansub.elasticsearch.model;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.OffsetDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(indexName = ElasticFansub.INDEX_NAME,aliases = {@Alias(alias = "fansub")})
@Mapping(mappingPath = "elastic/mappings/fansub.json")
@Setting(settingPath = "elastic/settings/fullTextSearchAnalyzerSettings.json")
public class ElasticFansub {
    public static final String INDEX_NAME = "fansub_v1";

    private String id;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime created;
    @Field(type = FieldType.Text, analyzer = "autocomplete_tr", searchAnalyzer = "autocomplete_tr_search")
    private String name;
}
