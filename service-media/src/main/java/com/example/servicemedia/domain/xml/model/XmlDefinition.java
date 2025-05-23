package com.example.servicemedia.domain.xml.model;

import com.example.servicemedia.util.persistance.AbstractEntity;
import com.example.servicemedia.domain.xml.enums.DefinitionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class XmlDefinition extends AbstractEntity {
    private String fileName;
    @Enumerated(EnumType.STRING)
    private DefinitionType type;
    @Lob
    @Column(nullable = false)
    private byte[] xmlFile;
    private Boolean success = Boolean.FALSE;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    private String JobExecutionId;
}
