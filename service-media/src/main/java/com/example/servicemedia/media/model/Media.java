package com.example.servicemedia.media.model;

import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.util.AbstractEntity;
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
public class Media extends AbstractEntity {
    private String name;
    private String description;
    private Integer count;
    @Temporal(TemporalType.DATE)
    private LocalDate publishDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;
    @OneToMany(mappedBy = "media",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<MediaSource> mediaSources;
    @Column(unique = true,nullable = false)
    private String slug;
}
