package com.example.servicemedia.xml.api;

import com.example.servicemedia.xml.mapper.XmlDefinitionApiMapper;
import com.example.servicemedia.xml.service.XmlDefinitionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("xml")
@RequiredArgsConstructor
public class XmlDefinitionController {
    private final XmlDefinitionServiceImpl service;

    @GetMapping
    public ResponseEntity<Page<XmlDefinitionResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(XmlDefinitionApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("start-job")
    public ResponseEntity<Void> startJob(@RequestParam String definition)  {
        service.startJob(definition);
        return ResponseEntity.ok().build();
    }

    @PostMapping("import")
    public ResponseEntity<Void> save(@RequestBody XmlDefinitionRequest request) throws IOException {
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
