package com.example.serviceusers.domain.usergroup.api;

import com.example.serviceusers.domain.user.model.Role;
import com.example.serviceusers.domain.usergroup.mapper.UserGroupApiMapper;
import com.example.serviceusers.domain.usergroup.service.UserGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user-groups")
@RequiredArgsConstructor
@Validated
public class UserGroupController {
    private final UserGroupService service;


    @GetMapping
    public ResponseEntity<Page<UserGroupResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(UserGroupApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserGroupResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(UserGroupApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping("filter")
    public ResponseEntity<Page<UserGroupResponse>> filter(Pageable pageable,
                                                     @RequestParam(required = false) String name,
                                                     @RequestParam(required = false) Role role) {
        return ResponseEntity.ok(UserGroupApiMapper.toPageResponse(service.filter(pageable,name,role)));
    }

    @PostMapping
    public ResponseEntity<UserGroupResponse> save(@Valid @RequestBody UserGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserGroupApiMapper.toResponse(service.save(UserGroupApiMapper.toDto(request))));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserGroupResponse> update(@PathVariable String id,@Valid @RequestBody UserGroupRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(UserGroupApiMapper.toResponse(service.update(id,UserGroupApiMapper.toDto(request))));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
