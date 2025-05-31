package com.example.servicemedia.util;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.servicemedia.domain.content.constants.ContentConstants;
import com.example.servicemedia.domain.content.model.Content;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CreatorComponent {
    public static String slugGenerator(String name) {
        return name
                .toLowerCase()
                .trim()
                .replaceAll("[^\\w\\s-]", "")
                .replaceAll("[\\s_-]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    public static String nameGenerator(int count, Content content) {
        final String name;
        switch (content.getType()) {
            case MOVIE -> name = content.getName();
            case SERIES -> name = content.getName() + " " + count + ContentConstants.EPISODE_PREFIX;
            default -> throw new IllegalStateException("Unexpected value: " + content.getType());
        }
        return name;
    }

    public static Query fullTextSearchQuery(String query) {
        return QueryBuilders.match()
                .field(ContentConstants.SEARCH_FIELD_NAME)
                .query(query)
                .fuzziness(ContentConstants.SEARCH_FUZZINESS)
                .build()
                ._toQuery();
    }
}
