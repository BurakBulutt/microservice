package com.example.serviceusers.domain.auth.service;


import com.example.serviceusers.config.security.jwt.JwtService;
import com.example.serviceusers.domain.auth.dto.AuthDto;
import com.example.serviceusers.domain.auth.dto.LoginDto;
import com.example.serviceusers.domain.auth.dto.RegisterDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class AuthServiceImpl {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
