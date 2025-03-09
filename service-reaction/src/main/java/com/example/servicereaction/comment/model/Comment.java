package com.example.servicereaction.comment.model;

import com.example.servicereaction.comment.enums.CommentType;
import com.example.servicereaction.util.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = {"commentList", "parent"})
public class Comment extends AbstractEntity {
    @Column(columnDefinition = "TEXT")
    private String content;
    private String userId;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "parent",cascade = CascadeType.ALL)
    private List<Comment> commentList;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentType type;
    private String targetId;
}
