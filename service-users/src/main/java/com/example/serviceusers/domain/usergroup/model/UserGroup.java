package com.example.serviceusers.domain.usergroup.model;

import com.example.serviceusers.domain.user.model.Role;
import com.example.serviceusers.domain.user.model.User;
import com.example.serviceusers.utilities.persistance.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;


import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity(name = UserGroup.TABLE)
public class UserGroup extends AbstractEntity {
    public static final String TABLE = "user_group";
    public static final String COL_NAME = "name";
    public static final String COL_ROLE = "role";

    @Column(name = COL_NAME, unique = true,nullable = false)
    private String name;
    @Column(name = COL_ROLE, unique = true,nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "userGroup",fetch = FetchType.LAZY)
    private List<User> userList;
}
