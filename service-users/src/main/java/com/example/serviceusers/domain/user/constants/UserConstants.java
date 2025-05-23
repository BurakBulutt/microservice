package com.example.serviceusers.domain.user.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConstants {
    public static final String USER_GROUP = "app-users-group";

    public static final String ACTION_VERIFY_EMAIL = "VERIFY_EMAIL";
    public static final String ACTION_UPDATE_PASSWORD = "UPDATE_PASSWORD";

    public static final String ATTRIBUTE_LOCALE = "locale";
    public static final String ATTRIBUTE_BIRTHDATE = "birthdate";

    public static final String CREDENTIALS_TYPE_PASSWORD = "password";

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    public static final String CACHE_NAME_USER="userCache";
    public static final String CACHE_NAME_USER_PAGE="userPageCache";
    public static final String CACHE_KEY_USER_ID="user-id:";
    public static final String CACHE_KEY_USER_USERNAME="user-username:";
    public static final String CACHE_KEY_USER_ALL="user-all:";
    public static final String CACHE_KEY_USER_FILTER="user-filter:";

    public static final String EXCEPTION_CANT_UPDATE_ADMIN = "Can not update admin";
    public static final String EXCEPTION_CANT_DELETE_ADMIN = "Can not delete admin";
    public static final String EXCEPTION_CANT_EXECUTE_EVENT_4_ADMIN = "Can not execute event for admin";
}
