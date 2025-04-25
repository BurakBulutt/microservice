package com.example.servicemedia.xml.api;

import com.example.servicemedia.xml.service.XmlDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("xml")
@RequiredArgsConstructor
public class XmlDefinitionController {
    private final XmlDefinitionService service;

    @PostMapping("import")
    public ResponseEntity<Void> save(@RequestBody XmlDefinitionRequest request) throws IOException {
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
