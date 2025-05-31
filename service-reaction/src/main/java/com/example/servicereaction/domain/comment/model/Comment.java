package com.example.servicereaction.domain.comment.model;

import com.example.servicereaction.domain.comment.elasticsearch.event.listener.ElasticCommentEventListener;
import com.example.servicereaction.domain.comment.enums.CommentTargetType;
import com.example.servicereaction.domain.comment.enums.CommentType;
import com.example.servicereaction.util.persistance.AbstractEntity;
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
@EntityListeners({ElasticCommentEventListener.class})
public class Comment extends AbstractEntity {
    @Column(columnDefinition = "TEXT",nullable = false)
    private String content;
    @Column(nullable = false)
    private String userId;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "parent",cascade = CascadeType.ALL)
    private List<Comment> commentList;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentType commentType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentTargetType targetType;
    @Column(nullable = false)
    private String targetId;
}
