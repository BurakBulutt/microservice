package com.example.servicemedia.xml.model;

import com.example.servicemedia.util.AbstractEntity;
import com.example.servicemedia.xml.enums.DefinitionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class XmlDefinition extends AbstractEntity {
    @Enumerated(EnumType.STRING)
    private DefinitionType type;
    private String xmlName;
    private Boolean success =  Boolean.FALSE;
}
