package com.example.servicemedia.domain.fansub.repo;

import com.example.servicemedia.domain.fansub.model.Fansub;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FansubRepository extends JpaRepository<Fansub, String>, JpaSpecificationExecutor<Fansub> {
    Optional<Fansub> findByNameContainsIgnoreCase(String name);

    List<Fansub> findAllByIdIn(Collection<String> ids, Sort sort);

}
