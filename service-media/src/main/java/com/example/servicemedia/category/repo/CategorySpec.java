package com.example.servicemedia.category.repo;

import com.example.servicemedia.category.model.Category;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

@NoArgsConstructor
public class CategorySpec {
    public static Specification<Category> nameContainsIgnoreCase(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            return cb.like(cb.upper(root.get("name")), "%" + name.toUpperCase(Locale.ROOT) + "%");
        };
    }
}
