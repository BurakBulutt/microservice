package com.example.servicemedia.xml.service;

import com.example.servicemedia.xml.api.XmlDefinitionRequest;
import com.example.servicemedia.xml.model.XmlDefinition;
import com.example.servicemedia.xml.repo.XmlDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class XmlDefinitionServiceImpl implements XmlDefinitionService {
    private final XmlDefinitionRepository repository;

    @Override
    public void save(XmlDefinitionRequest request) throws IOException {
        byte[] file = Base64.getDecoder().decode(request.base64());

        String fileName = request.fileName() + ".xml";

        String homeDir = System.getProperty("user.home");
        Path targetDir = Paths.get(homeDir, "XmlDefinitions");

        Path filePath = targetDir.resolve(fileName);

        Files.write(filePath, file);

        XmlDefinition xmlDefinition = new XmlDefinition();
        xmlDefinition.setType(request.type());
        xmlDefinition.setXmlName(request.fileName());

        repository.save(xmlDefinition);
    }
}
