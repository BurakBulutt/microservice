package com.example.servicemedia.domain.xml.api;

import com.example.servicemedia.domain.xml.mapper.XmlDefinitionApiMapper;
import com.example.servicemedia.domain.xml.service.XmlDefinitionServiceImpl;
import jakarta.validation.Valid;
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

    @GetMapping("{id}/start-job")
    public ResponseEntity<Void> startJob(@PathVariable String id)  {
        service.startJob(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("import")
    public ResponseEntity<Void> save(@RequestBody @Valid XmlDefinitionRequest request) throws IOException {
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
