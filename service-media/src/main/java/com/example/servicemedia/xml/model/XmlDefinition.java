package com.example.servicemedia.xml.model;

import com.example.servicemedia.util.AbstractEntity;
import com.example.servicemedia.xml.enums.DefinitionType;
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
    private String errorMessage;
}
