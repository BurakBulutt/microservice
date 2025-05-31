package com.example.servicemedia.domain.xml.repo;

import com.example.servicemedia.domain.xml.model.XmlDefinition;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface XmlDefinitionRepository extends JpaRepository<XmlDefinition,String> {
    List<XmlDefinition> findAllByIdIn(Collection<String> ids, Sort sort);
}
