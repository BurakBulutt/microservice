package com.example.servicemedia.content.model;

import com.example.servicemedia.content.enums.ContentType;
import com.example.servicemedia.util.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Content extends AbstractEntity {
    private String name;
    private String description;
    private String photoUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String subject;
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Column(unique = true,nullable = false)
    private String slug;
}
