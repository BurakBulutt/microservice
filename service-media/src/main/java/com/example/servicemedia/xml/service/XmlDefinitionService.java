package com.example.servicemedia.xml.service;

import com.example.servicemedia.xml.api.XmlDefinitionRequest;

import java.io.IOException;

public interface XmlDefinitionService {
    void save(XmlDefinitionRequest request) throws IOException;
}
