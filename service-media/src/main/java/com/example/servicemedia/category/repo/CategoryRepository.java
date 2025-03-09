package com.example.servicemedia.category.repo;

import com.example.servicemedia.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,String> {
    Optional<Category> findBySlug(String slug);
}
