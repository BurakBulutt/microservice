package com.example.servicemedia.domain.category.repo;

import com.example.servicemedia.domain.category.model.Category;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,String>, JpaSpecificationExecutor<Category> {
    Optional<Category> findBySlug(String slug);

    List<Category> findByNameContainsIgnoreCase(String name);

    List<Category> findAllByIdIn(Collection<String> ids, Sort sort);
}
