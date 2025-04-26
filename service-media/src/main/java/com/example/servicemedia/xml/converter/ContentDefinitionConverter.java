package com.example.servicemedia.xml.converter;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.content.enums.ContentType;
import com.example.servicemedia.media.dto.MediaDto;
import com.example.servicemedia.media.dto.MediaSourceDto;
import com.example.servicemedia.media.enums.SourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class ContentDefinitionConverter implements Converter<Element, ContentDto> {

    @Override
    public ContentDto convert(@NonNull Element element) {
        return ContentDto.builder()
                .name(parseText(element, "name"))
                .description(parseText(element, "description"))
                .photoUrl(parseText(element, "photoUrl"))
                .type(parseType(parseText(element, "type")))
                .subject(parseText(element, "subject"))
                .startDate(parseDate(parseText(element, "startDate")))
                .episodeTime(parseInteger(parseText(element, "episodeTime")))
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

    private Date parseDate(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (dateStr != null) {
            try {
                return format.parse(dateStr);
            } catch (ParseException e) {
                String msg = e.getLocalizedMessage();
                log.error("Date parsing failed: {}", msg);
                throw new RuntimeException(msg);
            }
        }
        log.warn("Date string is null");
        return null;
    }

    private List<CategoryDto> parseCategories(Element element) {
        Node node = element.getElementsByTagName("categoryList").item(0);
        List<CategoryDto> categoryList = new ArrayList<>();

        NodeList categoryNodes = ((Element) node).getElementsByTagName("category");

        for (int i = 0; i < categoryNodes.getLength(); i++) {
            Node categoryNode = categoryNodes.item(i);

            CategoryDto category = new CategoryDto();
            category.setName(categoryNode.getTextContent().trim());

            categoryList.add(category);
        }
        return categoryList;
    }

    private List<MediaDto> parseMedias(Element element) {
        Node node = element.getElementsByTagName("mediaList").item(0);
        List<MediaDto> mediaList = new ArrayList<>();

        NodeList mediaNodes = ((Element) node).getElementsByTagName("media");

        for (int i = 0; i < mediaNodes.getLength(); i++) {
            Node mediaNode = mediaNodes.item(i);

            Element mediaElement = (Element) mediaNode;

            MediaDto media = new MediaDto();
            media.setDescription(parseText(mediaElement, "description"));
            media.setCount(parseInteger(parseText(mediaElement, "count")));
            media.setPublishDate(parseDate(parseText(mediaElement, "publishDate")));
            media.setMediaSourceList(parseMediaSources(mediaElement));

            mediaList.add(media);
        }

        return mediaList;
    }

    private List<MediaSourceDto> parseMediaSources(Element element) {
        Node node = element.getElementsByTagName("mediaSourceList").item(0);
        List<MediaSourceDto> mediaSourceList = new ArrayList<>();

        NodeList mediaSourceNodes = ((Element) node).getElementsByTagName("mediaSource");
        for (int i = 0; i < mediaSourceNodes.getLength(); i++) {
            Node mediaSourceNode = mediaSourceNodes.item(i);

            Element mediaSourceElement = (Element) mediaSourceNode;

            SourceType sourceType = SourceType.valueOf(mediaSourceElement.getElementsByTagName("type").item(0).getTextContent());

            MediaSourceDto mediaSource = new MediaSourceDto();
            mediaSource.setUrl(parseText(mediaSourceElement, "url"));
            mediaSource.setType(sourceType);
            mediaSource.setFanSub(parseText(mediaSourceElement, "fanSub"));

            mediaSourceList.add(mediaSource);
        }
        return mediaSourceList;
    }

}
