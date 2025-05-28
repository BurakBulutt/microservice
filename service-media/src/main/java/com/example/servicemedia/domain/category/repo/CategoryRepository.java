package com.example.servicemedia.domain.category.repo;

import com.example.servicemedia.domain.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category,String>, JpaSpecificationExecutor<Category> {
    Optional<Category> findBySlug(String slug);

    List<Category> findByNameContainsIgnoreCase(String name);

    Page<Category> findAllByIdIn(Set<String> ids, Pageable pageable);
}
