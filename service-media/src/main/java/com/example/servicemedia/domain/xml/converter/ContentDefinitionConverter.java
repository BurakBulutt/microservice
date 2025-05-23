package com.example.servicemedia.domain.xml.converter;

import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.content.dto.ContentDto;
import com.example.servicemedia.domain.content.enums.ContentType;
import com.example.servicemedia.domain.fansub.dto.FansubDto;
import com.example.servicemedia.domain.media.dto.MediaDto;
import com.example.servicemedia.domain.media.dto.MediaSourceDto;
import com.example.servicemedia.domain.media.enums.SourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.servicemedia.domain.xml.constants.XmlConstants.*;

@Slf4j
public class ContentDefinitionConverter implements Converter<Element, ContentDto> {

    @Override
    public ContentDto convert(@NonNull Element element) {
        return ContentDto.builder()
                .name(parseText(element, CONTENT_NAME))
                .description(parseText(element, CONTENT_DESCRIPTION))
                .photoUrl(parseText(element, CONTENT_PHOTO_URL))
                .type(parseType(parseText(element, CONTENT_TYPE)))
                .subject(parseText(element, CONTENT_SUBJECT))
                .startDate(parseDate(parseText(element, CONTENT_START_DATE)))
                .episodeTime(parseInteger(parseText(element, CONTENT_EPISODE_TIME)))
                .categories(parseCategories(element))
                .medias(parseMedias(element))
                .build();
    }

    private String parseText(Element element, String tag) {
        Node node = element.getElementsByTagName(tag).item(0);
        return node.getTextContent();
    }

    private ContentType parseType(String typeStr) {
        if (typeStr != null) {
            try {
                return ContentType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown content type received: {}", typeStr);
                return null;
            }
        }
        log.warn("Content type string is null");
        return null;
    }

    private Integer parseInteger(String integerStr) {
        if (integerStr != null) {
            return Integer.parseInt(integerStr);
        }
        log.warn("Integer string is null");
        return null;
    }

    private LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        if (dateStr != null) {
            return LocalDate.parse(dateStr, formatter);
        }
        log.warn("Date string is null");
        return null;
    }

    private List<CategoryDto> parseCategories(Element element) {
        Node node = element.getElementsByTagName(CONTENT_CATEGORY_LIST).item(0);
        List<CategoryDto> categoryList = new ArrayList<>();

        NodeList categoryNodes = ((Element) node).getElementsByTagName(CATEGORY);

        for (int i = 0; i < categoryNodes.getLength(); i++) {
            Node categoryNode = categoryNodes.item(i);

            CategoryDto category = new CategoryDto();
            category.setName(categoryNode.getTextContent().trim());

            categoryList.add(category);
        }
        return categoryList;
    }

    private List<MediaDto> parseMedias(Element element) {
        Node node = element.getElementsByTagName(CONTENT_MEDIA_LIST).item(0);
        List<MediaDto> mediaList = new ArrayList<>();

        NodeList mediaNodes = ((Element) node).getElementsByTagName(MEDIA);

        for (int i = 0; i < mediaNodes.getLength(); i++) {
            Node mediaNode = mediaNodes.item(i);

            Element mediaElement = (Element) mediaNode;

            MediaDto media = new MediaDto();
            media.setDescription(parseText(mediaElement, MEDIA_DESCRIPTION));
            media.setCount(parseInteger(parseText(mediaElement, MEDIA_COUNT)));
            media.setPublishDate(parseDate(parseText(mediaElement, MEDIA_PUBLISH_DATE)));
            media.setMediaSourceList(parseMediaSources(mediaElement));

            mediaList.add(media);
        }

        return mediaList;
    }

    private List<MediaSourceDto> parseMediaSources(Element element) {

        if (element.getElementsByTagName(MEDIA_MEDIA_SOURCE_LIST).item(0) == null) {
            return Collections.emptyList();
        }

        Node node = element.getElementsByTagName(MEDIA_MEDIA_SOURCE_LIST).item(0);
        List<MediaSourceDto> mediaSourceList = new ArrayList<>();

        NodeList mediaSourceNodes = ((Element) node).getElementsByTagName(MEDIA_SOURCE);
        for (int i = 0; i < mediaSourceNodes.getLength(); i++) {
            Node mediaSourceNode = mediaSourceNodes.item(i);

            Element mediaSourceElement = (Element) mediaSourceNode;

            SourceType sourceType = SourceType.valueOf(mediaSourceElement.getElementsByTagName(MEDIA_SOURCE_TYPE).item(0).getTextContent());

            MediaSourceDto mediaSource = new MediaSourceDto();
            mediaSource.setUrl(parseText(mediaSourceElement, MEDIA_SOURCE_URL));
            mediaSource.setType(sourceType);
            mediaSource.setFansub(FansubDto.builder()
                    .name(parseText(mediaSourceElement, MEDIA_SOURCE_FANSUB))
                    .build());

            mediaSourceList.add(mediaSource);
        }

        return mediaSourceList;
    }

}
