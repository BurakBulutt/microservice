package com.example.servicemedia.domain.xml.elasticsearch.model;

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
//@Document(indexName = "xml_definition",aliases = {@Alias(alias = "xml_definition")})
@Document(indexName = "xml_definition")
@Mapping(mappingPath = "elastic/mappings/xmlDefinition.json")
@Setting(settingPath = "elastic/settings/fullTextSearchAnalyzerSettings.json")
public class ElasticXmlDefinition {
    private String id;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime created;
    @Field(type = FieldType.Text, analyzer = "autocomplete_tr", searchAnalyzer = "autocomplete_tr_search")
    private String fileName;
    @Field(type = FieldType.Boolean)
    private Boolean success;
    @Field(type = FieldType.Keyword)
    private String jobExecutionId;
    @Field(type = FieldType.Keyword)
    private String type;
}
