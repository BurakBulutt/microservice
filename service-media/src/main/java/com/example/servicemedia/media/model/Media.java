package com.example.servicemedia.media.model;

import com.example.servicemedia.util.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Media extends AbstractEntity {
    private String name;
    private String description;
    @Column(nullable = false)
    private String contentId;
    private Integer count;
    @Temporal(TemporalType.DATE)
    private Date publishDate;
    @OneToMany(mappedBy = "media",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<MediaSource> mediaSources;
    private String slug;
}
