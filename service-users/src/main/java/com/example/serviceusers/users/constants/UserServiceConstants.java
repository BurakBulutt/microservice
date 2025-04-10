package com.example.serviceusers.users.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceConstants {
    public static final String USER_GROUP = "app-users-group";
    public static final String ACTION_VERIFY_EMAIL = "VERIFY_EMAIL";
    public static final String ACTION_UPDATE_PASSWORD = "UPDATE_PASSWORD";
    public static final String ATTRIBUTE_LOCALE = "locale";
    public static final String ATTRIBUTE_BIRTHDATE = "birthdate";
    public static final String CREDENTIALS_TYPE_PASSWORD = "password";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
}
