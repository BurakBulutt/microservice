package com.example.servicereaction.domain.like.model;

import com.example.servicereaction.domain.like.enums.LikeTarget;
import com.example.servicereaction.domain.like.enums.LikeType;
import com.example.servicereaction.util.persistance.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "likes",uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "targetId"}))
public class Like extends AbstractEntity {
    @Column(nullable = false)
    private String userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikeType likeType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikeTarget likeTarget;
    @Column(nullable = false)
    private String targetId;
}
