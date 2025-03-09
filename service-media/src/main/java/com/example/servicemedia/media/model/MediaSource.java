package com.example.servicemedia.media.model;

import com.example.servicemedia.media.enums.SourceType;
import com.example.servicemedia.util.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"type","media_id","fan_sub"})})
public class MediaSource extends AbstractEntity {
    @Lob
    @Column(columnDefinition = "TEXT")
    private String url;
    @Enumerated(EnumType.STRING)
    private SourceType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id",nullable = false)
    private Media media;
    private String fanSub;
}
