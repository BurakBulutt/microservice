package com.example.servicemedia.domain.fansub.model;

import com.example.servicemedia.domain.fansub.elasticsearch.event.listener.ElasticFansubEventListener;
import com.example.servicemedia.domain.media.model.MediaSource;
import com.example.servicemedia.util.persistance.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners({ElasticFansubEventListener.class})
public class Fansub extends AbstractEntity {
    @Column(unique = true,nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String url;
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "fansub",orphanRemoval = true)
    private List<MediaSource> mediaSources;
}
