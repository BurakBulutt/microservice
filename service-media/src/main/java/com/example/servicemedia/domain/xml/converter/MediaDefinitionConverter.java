package com.example.servicemedia.domain.xml.converter;

import com.example.servicemedia.domain.content.dto.ContentDto;
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
public class MediaDefinitionConverter implements Converter<Element, MediaDto> {

    @Override
    public MediaDto convert(@NonNull Element element) {
        return MediaDto.builder()
                .description(parseText(element, MEDIA_DESCRIPTION))
                .count(parseInteger(parseText(element, MEDIA_COUNT)))
                .publishDate(parseDate(parseText(element, MEDIA_PUBLISH_DATE)))
                .content(ContentDto.builder().id(parseText(element, MEDIA_CONTENT_ID)).build())
                .mediaSourceList(parseMediaSources(element))
                .build();
    }

    private String parseText(Element element, String tag) {
        Node node = element.getElementsByTagName(tag).item(0);
        return node != null ? node.getTextContent() : null;
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
        log.warn("Source type is null");
        return null;
    }

    private Integer parseInteger(String integerStr) {
        if (integerStr != null) {
            return Integer.parseInt(integerStr);
        }
        log.warn("Integer is null");
        return null;
    }

    private LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        if (dateStr != null) {
            return LocalDate.parse(dateStr,formatter);
        }
        log.warn("Date is null");
        return null;
    }

    private List<MediaSourceDto> parseMediaSources(Element element) {
        Node node = element.getElementsByTagName(MEDIA_MEDIA_SOURCE_LIST).item(0);

        if (node == null) {
            log.warn("Media sources is null");
            return Collections.emptyList();
        }

        List<MediaSourceDto> mediaSourceList = new ArrayList<>();

        NodeList mediaSourceNodes = ((Element) node).getElementsByTagName(MEDIA_SOURCE);

        for (int i = 0; i < mediaSourceNodes.getLength(); i++) {
            Node mediaSourceNode = mediaSourceNodes.item(i);

            Element mediaSourceElement = (Element) mediaSourceNode;

            SourceType sourceType = parseType(parseText(mediaSourceElement,MEDIA_SOURCE_TYPE));

            Node fansubNode = mediaSourceElement.getElementsByTagName(MEDIA_SOURCE_FANSUB).item(0);

            FansubDto fansub = new FansubDto();
            fansub.setName(fansubNode.getTextContent().trim());

            MediaSourceDto mediaSource = new MediaSourceDto();
            mediaSource.setUrl(parseText(mediaSourceElement, MEDIA_SOURCE_URL));
            mediaSource.setType(sourceType);
            mediaSource.setFansub(fansub);

            mediaSourceList.add(mediaSource);
        }
        return mediaSourceList;
    }
}
