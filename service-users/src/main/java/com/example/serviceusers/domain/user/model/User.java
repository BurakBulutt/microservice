package com.example.serviceusers.domain.user.model;

import com.example.serviceusers.auditlistener.service.EntityLogListener;
import com.example.serviceusers.domain.user.elasticsearch.event.listener.ElasticUserEventListener;
import com.example.serviceusers.domain.usergroup.model.UserGroup;
import com.example.serviceusers.utilities.persistance.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity(name = User.TABLE)
@EntityListeners(ElasticUserEventListener.class)
public class User extends AbstractEntity implements UserDetails {
    public static final String TABLE = "users";
    public static final String COL_FIRST_NAME = "firstName";
    public static final String COL_LAST_NAME = "lastName";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_EMAIL = "email";
    public static final String COL_ROLE = "role";
    public static final String COL_IS_VERIFIED = "isVerified";
    public static final String COL_IS_ENABLED = "isEnabled";
    public static final String COL_USER_GROUP_ID = "user_group_id";

    @Column(name = COL_FIRST_NAME)
    private String firstName;
    @Column(name = COL_LAST_NAME)
    private String lastName;
    @Column(name = COL_USERNAME,nullable = false,unique = true)
    private String username;
    @Column(name = COL_PASSWORD,nullable = false)
    private String password;
    @Column(name = COL_EMAIL,nullable = false,unique = true)
    private String email;
    @Column(name = COL_ROLE)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = COL_IS_ENABLED,nullable = false)
    private Boolean isEnabled = Boolean.FALSE;
    @Column(name = COL_IS_VERIFIED,nullable = false)
    private Boolean isVerified =  Boolean.FALSE;

    @ManyToOne
    @JoinColumn(name = COL_USER_GROUP_ID, nullable = true)
    private UserGroup userGroup;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
