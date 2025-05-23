package com.example.servicemedia.domain.media.model;

import com.example.servicemedia.domain.fansub.model.Fansub;
import com.example.servicemedia.domain.media.enums.SourceType;
import com.example.servicemedia.util.persistance.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"type","media_id","fansub_id"})})
public class MediaSource extends AbstractEntity {
    @Column(columnDefinition = "TEXT",nullable = false)
    private String url;
    @Enumerated(EnumType.STRING)
    private SourceType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id",nullable = false)
    private Media media;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fansub_id",nullable = false)
    private Fansub fansub;
}
