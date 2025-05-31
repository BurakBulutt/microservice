package com.example.servicemedia.domain.media.model;

import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.domain.media.elasticsearch.event.listener.ElasticMediaEventListener;
import com.example.servicemedia.util.persistance.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners({ElasticMediaEventListener.class})
public class Media extends AbstractEntity {
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private Integer count;
    @Temporal(TemporalType.DATE)
    private LocalDate publishDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id",nullable = false)
    private Content content;
    @OneToMany(mappedBy = "media",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<MediaSource> mediaSources;
    @Column(unique = true,nullable = false)
    private String slug;
}
