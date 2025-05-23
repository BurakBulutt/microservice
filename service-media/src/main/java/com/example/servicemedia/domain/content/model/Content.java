package com.example.servicemedia.domain.content.model;

import com.example.servicemedia.domain.category.model.Category;
import com.example.servicemedia.domain.content.enums.ContentType;
import com.example.servicemedia.domain.media.model.Media;
import com.example.servicemedia.util.persistance.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Content extends AbstractEntity {
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(columnDefinition = "TEXT")
    private String photoUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String subject;
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;
    @Column(unique = true,nullable = false)
    private String slug;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "category_content",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;
    @OneToMany(mappedBy = "content",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Media> medias;
    private Integer episodeTime;
}
