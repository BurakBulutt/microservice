package com.example.servicemedia.domain.xml.model;

import com.example.servicemedia.domain.xml.elasticsearch.event.listener.ElasticXmlDefinitionEventListener;
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
@EntityListeners({ElasticXmlDefinitionEventListener.class})
public class XmlDefinition extends AbstractEntity {
    private String fileName;
    @Enumerated(EnumType.STRING)
    private DefinitionType type;
    @Lob
    @Column(nullable = false)
    private byte[] xmlFile;
    private Boolean success = Boolean.FALSE;
    @Column(length = 2500)
    private String errorMessage;
    private Long JobExecutionId;
}
