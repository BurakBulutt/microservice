package com.example.servicemedia.xml.converter;

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
public class MediaDefinitionConverter implements Converter<Element, MediaDto> {

    @Override
    public MediaDto convert(@NonNull Element element) {
        return MediaDto.builder()
                .description(parseText(element, "description"))
                .count(parseInteger(parseText(element, "count")))
                .publishDate(parseDate(parseText(element, "publishDate")))
                .content(ContentDto.builder().id(parseText(element, "contentId")).build())
                .mediaSourceList(parseMediaSources(element))
                .build();
    }

    private String parseText(Element element, String tag) {
        Node node = element.getElementsByTagName(tag).item(0);
        return node.getTextContent();
    }

    private SourceType parseType(String typeStr) {
        if (typeStr != null) {
            try {
                return SourceType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown source type received: {}", typeStr);
                return null;
            }
        }
        log.warn("Source type string is null");
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

    private List<MediaSourceDto> parseMediaSources(Element element) {
        Node node = element.getElementsByTagName("mediaSourceList").item(0);
        List<MediaSourceDto> mediaSourceList = new ArrayList<>();

        NodeList mediaSourceNodes = ((Element) node).getElementsByTagName("mediaSource");
        for (int i = 0; i < mediaSourceNodes.getLength(); i++) {
            Node mediaSourceNode = mediaSourceNodes.item(i);

            Element mediaSourceElement = (Element) mediaSourceNode;

            SourceType sourceType = parseType(parseText(mediaSourceElement,"type"));

            MediaSourceDto mediaSource = new MediaSourceDto();
            mediaSource.setUrl(parseText(mediaSourceElement, "url"));
            mediaSource.setType(sourceType);
            mediaSource.setFanSub(parseText(mediaSourceElement, "fanSub"));

            mediaSourceList.add(mediaSource);
        }
        return mediaSourceList;
    }
}
