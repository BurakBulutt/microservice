package com.example.servicemedia.domain.content.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentConstants {
    public static final String CACHE_NAME_CONTENT="contentCache";
    public static final String CACHE_NAME_CONTENT_PAGE="contentPageCache";

    public static final String EPISODE_PREFIX = ". Bölüm";

    public static final String ELASTIC_CONTENT_INDEX = "content";

    public static final String SEARCH_FIELD_NAME = "name";
    public static final String SEARCH_FUZZINESS = "AUTO";
    public static final String HIGHLIGHT_PRE_TAG = "<strong>";
    public static final String HIGHLIGHT_POST_TAG = "</strong>";
}
