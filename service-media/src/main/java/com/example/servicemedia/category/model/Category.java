package com.example.servicemedia.category.model;

import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.util.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends AbstractEntity {
    private String name;
    private String description;
    @Column(unique = true,nullable = false)
    private String slug;
    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "categories")
    private List<Content> contentList;
}
