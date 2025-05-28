package com.example.servicemedia.domain.xml.repo;

import com.example.servicemedia.domain.xml.model.XmlDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface XmlDefinitionRepository extends JpaRepository<XmlDefinition,String> {
    Page<XmlDefinition> findAllByIdIn(Collection<String> ids, Pageable pageable);
}
