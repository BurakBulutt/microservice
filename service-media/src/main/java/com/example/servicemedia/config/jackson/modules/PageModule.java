package com.example.servicemedia.config.jackson.modules;

import com.example.servicemedia.config.jackson.deserializers.GenericPageDeserializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public final class PageModule extends SimpleModule {

    public PageModule() {
        super("PageModule", Version.unknownVersion());
        init();
    }

    public void init() {
        this.addAbstractTypeMapping(Page.class, PageImpl.class);

        this.addDeserializer(PageImpl.class, new GenericPageDeserializer());
    }
}
