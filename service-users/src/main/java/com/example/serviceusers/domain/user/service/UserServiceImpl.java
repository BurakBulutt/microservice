package com.example.serviceusers.domain.user.service;


import com.example.serviceusers.domain.user.constants.UserConstants;
import com.example.serviceusers.domain.user.dto.UserDto;
import com.example.serviceusers.domain.user.elasticsearch.model.ElasticUser;
import com.example.serviceusers.domain.user.mapper.UserServiceMapper;
import com.example.serviceusers.domain.user.model.User;
import com.example.serviceusers.domain.user.repo.UserRepository;
import com.example.serviceusers.domain.usergroup.service.UserGroupService;
import com.example.serviceusers.utilities.exception.BaseException;
import com.example.serviceusers.utilities.exception.MessageResource;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = UserConstants.CACHE_NAME_USER)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupService userGroupService;
    private final ElasticsearchOperations elasticsearchOperations;


    @Override
    @Cacheable(value = UserConstants.CACHE_NAME_USER_PAGE, key = "'user-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()")
    public Page<UserDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(UserServiceMapper::toDto);
    }

    @Override
    @Cacheable(value = UserConstants.CACHE_NAME_USER_PAGE, key = "'user-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()",condition = "#username == null and #isEnabled == null and #isVerified == null")
    public Page<UserDto> filter(Pageable pageable, String username, Boolean isEnabled, Boolean isVerified) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();

        if (username != null && !username.isBlank()) {
            queryBuilder.must(QueryBuilders.match()
                    .field("username")
                    .query(username)
                    .build()
                    ._toQuery());
        }

        if (isEnabled != null) {
            queryBuilder.filter(QueryBuilders.term()
                    .field("isEnabled")
                    .value(isEnabled)
                    .build()
                    ._toQuery());
        }

        if (isVerified != null) {
            queryBuilder.filter(QueryBuilders.term()
                    .field("isVerified")
                    .value(isVerified)
                    .build()
                    ._toQuery());
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();
        SearchHits<ElasticUser> search = elasticsearchOperations.search(nativeQuery, ElasticUser.class);
        Set<String> ids = search.getSearchHits().stream()
                .map(hit -> hit.getContent().getId())
                .collect(Collectors.toSet());
        return new PageImpl<>(repository.findAllByIdIn(ids, nativeQuery.getSort()), pageable, search.getTotalHits())
                .map(UserServiceMapper::toDto);
    }

    @Override
    @Cacheable(key = "'user-id:' + #id")
    public UserDto getById(String id) {
        return repository.findById(id).map(UserServiceMapper::toDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, User.class.getSimpleName(), id));
    }

    @Override
    public UserDto getByUsername(String username) {
        return repository.findByUsername(username).map(UserServiceMapper::toDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, User.class.getSimpleName(), username));
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'user-id:' + #result.id")
            },
            evict = @CacheEvict(value = UserConstants.CACHE_NAME_USER_PAGE, allEntries = true)
    )
    public UserDto save(UserDto userDto) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (userDto.getUserGroupDto() != null) {
            user.setUserGroup(userGroupService.findById(userDto.getUserGroupDto().getId()));
        }

        return UserServiceMapper.toDto(repository.save(UserServiceMapper.toEntity(user, userDto)));
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'user-id:' + #id")
            },
            evict = @CacheEvict(value = UserConstants.CACHE_NAME_USER_PAGE, allEntries = true)
    )
    public UserDto update(String id, UserDto userDto) {
        User user = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, User.class.getSimpleName(), id));

        if (userDto.getUserGroupDto() != null) {
            user.setUserGroup(userGroupService.findById(userDto.getUserGroupDto().getId()));
        } else {
            user.setUserGroup(null);
        }

        return UserServiceMapper.toDto(repository.save(UserServiceMapper.toEntity(user, userDto)));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = UserConstants.CACHE_NAME_USER_PAGE, allEntries = true),
            @CacheEvict(key = "'user-id:' + #id")
    })
    public void delete(String id) {
        User user = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, User.class.getSimpleName(), id));
        repository.delete(user);
    }

    @Override
    @Cacheable(key = "'user-details:' + #id")
    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, User.class.getSimpleName(), username));
    }

    @Override
    public Long count() {
        return repository.count();
    }
}
