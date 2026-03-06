package com.example.serviceusers.domain.user.elasticsearch.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.OffsetDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(indexName = ElasticUser.INDEX_NAME)
@Setting(settingPath = "elastic/settings/fullTextSearchAnalyzerSettings.json")
public class ElasticUser {
    public static final String INDEX_NAME = "user";

    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime created;
    @Field(type = FieldType.Text, analyzer = "autocomplete_tr", searchAnalyzer = "autocomplete_tr_search")
    private String username;
    @Field(type = FieldType.Boolean)
    private Boolean isEnabled;
    @Field(type = FieldType.Boolean)
    private Boolean isVerified;
}
