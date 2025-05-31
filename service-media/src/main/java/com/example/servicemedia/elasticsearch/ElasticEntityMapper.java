package com.example.servicemedia.elasticsearch;

import com.example.servicemedia.domain.category.elasticsearch.model.ElasticCategory;
import com.example.servicemedia.domain.category.model.Category;
import com.example.servicemedia.domain.content.elasticsearch.model.ElasticContent;
import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.domain.fansub.elasticsearch.model.ElasticFansub;
import com.example.servicemedia.domain.fansub.model.Fansub;
import com.example.servicemedia.domain.media.elasticsearch.model.ElasticMedia;
import com.example.servicemedia.domain.media.model.Media;
import com.example.servicemedia.domain.xml.elasticsearch.model.ElasticXmlDefinition;
import com.example.servicemedia.domain.xml.model.XmlDefinition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElasticEntityMapper {

    public static ElasticCategory toElasticCategory(Category category) {
        return ElasticCategory.builder()
                .id(category.getId())
                .created(category.getCreated().atOffset(ZoneOffset.UTC))
                .name(category.getName())
                .slug(category.getSlug())
                .build();
    }

    public static ElasticContent toElasticContent(Content content) {
        List<String> categoryIds = new ArrayList<>();

        if (content.getCategories() != null && !content.getCategories().isEmpty()) {
            categoryIds.addAll(content.getCategories().stream().map(Category::getId).collect(Collectors.toSet()));
        }

        return ElasticContent.builder()
                .id(content.getId())
                .created(content.getCreated().atOffset(ZoneOffset.UTC))
                .name(content.getName())
                .photoUrl(content.getPhotoUrl())
                .slug(content.getSlug())
                .type(content.getType().name())
                .startDate(content.getStartDate())
                .categories(categoryIds)
                .build();
    }

    public static ElasticFansub toElasticFansub(Fansub fansub) {
        return ElasticFansub.builder()
                .id(fansub.getId())
                .created(fansub.getCreated().atOffset(ZoneOffset.UTC))
                .name(fansub.getName())
                .build();
    }

    public static ElasticMedia toElasticMedia(Media media) {
        return ElasticMedia.builder()
                .id(media.getId())
                .created(media.getCreated().atOffset(ZoneOffset.UTC))
                .name(media.getName())
                .slug(media.getSlug())
                .count(media.getCount())
                .contentId(media.getContent() != null ? media.getContent().getId() : null)
                .publishDate(media.getPublishDate())
                .build();
    }

    public static ElasticXmlDefinition toElasticXmlDefinition(XmlDefinition xmlDefinition) {
        return ElasticXmlDefinition.builder()
                .id(xmlDefinition.getId())
                .created(xmlDefinition.getCreated().atOffset(ZoneOffset.UTC))
                .fileName(xmlDefinition.getFileName())
                .type(xmlDefinition.getType().name())
                .success(xmlDefinition.getSuccess())
                .build();
    }
}
