package com.example.serviceusers.domain.user.api;

import com.example.serviceusers.domain.user.mapper.UserApiMapper;
import com.example.serviceusers.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(UserApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(UserApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping("username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<UserResponse> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(UserApiMapper.toResponse(service.getByUsername(username)));
    }

    @GetMapping("filter")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Page<UserResponse>> filter(Pageable pageable,
                                                     @RequestParam(required = false) String username,
                                                     @RequestParam(required = false) Boolean isEnabled,
                                                     @RequestParam(required = false) Boolean isVerified) {
        return ResponseEntity.ok(UserApiMapper.toPageResponse(service.filter(pageable,username,isEnabled,isVerified)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<UserResponse> save(@Valid @RequestBody SaveUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserApiMapper.toResponse(service.save(UserApiMapper.toDto(request))));
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<UserResponse> update(@PathVariable String id,@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(UserApiMapper.toResponse(service.update(id,UserApiMapper.toDto(request))));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("count")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Long> getCount() {
        return ResponseEntity.ok(service.count());
    }
}
