package com.example.serviceusers.domain.auth.service;


import com.example.serviceusers.config.security.jwt.JwtService;
import com.example.serviceusers.domain.auth.api.UpdateUserRequest;
import com.example.serviceusers.domain.auth.dto.AuthDto;
import com.example.serviceusers.domain.auth.dto.LoginDto;
import com.example.serviceusers.domain.auth.dto.RegisterDto;
import com.example.serviceusers.domain.user.dto.UserDto;
import com.example.serviceusers.domain.user.mapper.UserServiceMapper;
import com.example.serviceusers.domain.user.model.Role;
import com.example.serviceusers.domain.user.model.User;
import com.example.serviceusers.domain.user.repo.UserRepository;
import com.example.serviceusers.domain.usergroup.model.UserGroup;
import com.example.serviceusers.utilities.exception.BaseException;
import com.example.serviceusers.utilities.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class AuthServiceImpl {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtDecoder jwtDecoder;

    public AuthDto login(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BaseException(MessageResource.BAD_REQUEST);
        }

        if (!user.isEnabled()) {
            throw new BaseException(MessageResource.FORBIDDEN);
        }

        return buildAuthDto(user);
    }

    @Transactional
    public AuthDto register(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new BaseException(MessageResource.CONFLICT);
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BaseException(MessageResource.CONFLICT);
        }

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(Role.USER);
        user.setIsEnabled(Boolean.TRUE);
        user.setIsVerified(Boolean.FALSE);

        //TODO User verify  flow.

        return buildAuthDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateProfile(String id,UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        userRepository.save(user);

        return UserServiceMapper.toDto(user);
    }


    public UserDto extractUser(){
        UserDto user = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes instanceof ServletRequestAttributes) {
           final String token =  ((ServletRequestAttributes) requestAttributes).getRequest().getHeader("Authorization");

           final String tokenValue = extractToken(token);

           Map<String,Object> claims = jwtDecoder.decode(tokenValue).getClaims();

           String subject = (String) claims.get("sub");

           user = userRepository.findByUsername(subject).map(UserServiceMapper::toDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,User.class.getSimpleName(),subject));

        }else {
            throw new BaseException(MessageResource.INTERNAL_SERVER_ERROR);
        }

        return user;
    }

    private String extractToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BaseException(MessageResource.UNAUTHORIZED);
        }
        return token.substring(7);
    }

    private AuthDto buildAuthDto(User user) {
        UserGroup userGroup = user.getUserGroup();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        if (userGroup != null) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority(userGroup.getRole().name()));
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                authorities
        );
        String token = jwtService.generateToken(authentication);
        return AuthDto.builder()
                .token(token)
                .userDto(UserServiceMapper.toUserDto(user))
                .build();
    }
}
