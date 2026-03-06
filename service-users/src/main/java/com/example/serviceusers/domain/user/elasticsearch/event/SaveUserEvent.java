package com.example.serviceusers.domain.user.elasticsearch.event;

import com.example.serviceusers.domain.user.model.User;

public record SaveUserEvent(
        User user
) {
}
