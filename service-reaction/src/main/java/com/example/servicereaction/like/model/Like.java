package com.example.servicereaction.like.model;

import com.example.servicereaction.like.enums.LikeType;
import com.example.servicereaction.util.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "likes",uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "targetId"}))
public class Like extends AbstractEntity {
    private String userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikeType likeType;
    private String targetId;
}
