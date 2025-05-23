package com.example.servicemedia.domain.content.repo;

import com.example.servicemedia.domain.category.model.Category;
import com.example.servicemedia.domain.content.model.Content;
import jakarta.persistence.criteria.Join;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentSpec {

    public static Specification<Content> byCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) return null;

            Join<Content, Category> join = root.join("categories");

            return cb.equal(join.get("id"), category);
        };
    }
}
