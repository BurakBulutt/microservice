package com.example.servicemedia.category.repo;

import com.example.servicemedia.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,String>, JpaSpecificationExecutor<Category> {
    Optional<Category> findBySlug(String slug);

}
