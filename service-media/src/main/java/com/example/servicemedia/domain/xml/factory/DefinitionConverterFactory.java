package com.example.servicemedia.domain.xml.factory;

import com.example.servicemedia.domain.xml.converter.ContentDefinitionConverter;
import com.example.servicemedia.domain.xml.converter.MediaDefinitionConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefinitionConverterFactory {

    public static ContentDefinitionConverter getContentConverter() {
        return new ContentDefinitionConverter();
    }

    public static MediaDefinitionConverter getMediaConverter() {
        return new MediaDefinitionConverter();
    }
}
