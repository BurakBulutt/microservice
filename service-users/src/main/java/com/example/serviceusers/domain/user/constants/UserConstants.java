package com.example.serviceusers.domain.user.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConstants {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String CACHE_NAME_USER="userCache";
    public static final String CACHE_NAME_USER_PAGE="userPageCache";
    public static final String CACHE_KEY_USER_ID="user-id:";
    public static final String CACHE_KEY_USER_USERNAME="user-username:";
    public static final String CACHE_KEY_USER_ALL="user-all:";
    public static final String CACHE_KEY_USER_FILTER="user-filter:";
}
