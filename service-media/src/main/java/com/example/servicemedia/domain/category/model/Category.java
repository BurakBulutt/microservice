package com.example.servicemedia.domain.category.model;

import com.example.servicemedia.domain.category.elasticsearch.event.listener.ElasticCategoryEventListener;
import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.util.persistance.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ElasticCategoryEventListener.class})
public class Category extends AbstractEntity {
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(unique = true,nullable = false)
    private String slug;
    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "categories")
    private List<Content> contentList;
}
