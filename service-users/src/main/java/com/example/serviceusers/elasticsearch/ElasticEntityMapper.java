package com.example.serviceusers.elasticsearch;


import com.example.serviceusers.domain.user.elasticsearch.model.ElasticUser;
import com.example.serviceusers.domain.user.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElasticEntityMapper {

    public static ElasticUser toElasticUser(User user) {
        return ElasticUser.builder()
                .id(user.getId())
                .created(user.getCreated().atOffset(ZoneOffset.UTC))
                .username(user.getUsername())
                .isEnabled(user.getIsEnabled())
                .isVerified(user.getIsVerified())
                .build();
    }
}
