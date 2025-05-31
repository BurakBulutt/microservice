package com.example.servicereaction.domain.comment.elasticsearch.model;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.OffsetDateTime;

@Document(indexName = "comment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Mapping(mappingPath = "elastic/mappings/comment.json")
public class ElasticComment {
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime created;
    @Field(type = FieldType.Keyword)
    private String userId;
    @Field(type = FieldType.Keyword)
    private String parentId;
    @Field(type = FieldType.Keyword)
    private String targetId;
    @Field(type = FieldType.Keyword)
    private String targetType;
    @Field(type = FieldType.Keyword)
    private String commentType;
}
