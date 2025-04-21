package com.example.servicemedia.media.repo;

import com.example.servicemedia.media.model.Media;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MediaSpec {

    public static Specification<Media> byContentId(String contentId) {
        return (root, query, cb) -> {
            if (contentId == null || contentId.isBlank()) return null;
            return cb.equal(root.get("content").get("id"), contentId);
        };
    }

    public static Specification<Media> nameContainsIgnoreCase(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            return cb.like(cb.upper(root.get("name")), "%" + name.toUpperCase(Locale.ROOT) + "%");
        };
    }

}
