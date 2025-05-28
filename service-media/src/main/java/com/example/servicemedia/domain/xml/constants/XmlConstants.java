package com.example.servicemedia.domain.xml.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlConstants {
    public static final String CONTENT = "content";
    public static final String CONTENT_NAME = "name";
    public static final String CONTENT_DESCRIPTION = "description";
    public static final String CONTENT_PHOTO_URL = "photoUrl";
    public static final String CONTENT_TYPE = "type";
    public static final String CONTENT_SUBJECT = "subject";
    public static final String CONTENT_START_DATE = "startDate";
    public static final String CONTENT_EPISODE_TIME = "episodeTime";
    public static final String CONTENT_CATEGORY_LIST = "categoryList";
    public static final String CONTENT_MEDIA_LIST = "mediaList";

    public static final String CATEGORY = "category";

    public static final String MEDIA = "media";
    public static final String MEDIA_DESCRIPTION = "description";
    public static final String MEDIA_COUNT = "count";
    public static final String MEDIA_PUBLISH_DATE = "publishDate";
    public static final String MEDIA_CONTENT_ID = "contentId";
    public static final String MEDIA_MEDIA_SOURCE_LIST = "mediaSourceList";

    public static final String MEDIA_SOURCE = "mediaSource";
    public static final String MEDIA_SOURCE_TYPE = "type";
    public static final String MEDIA_SOURCE_URL = "url";
    public static final String MEDIA_SOURCE_FANSUB = "fanSub";

    public static final String BATCH_IMPORT_XML_JOB = "importXmlJob";
    public static final String BATCH_IMPORT_XML_STEP = "importXmlStep";
    public static final String BATCH_IMPORT_XML_TASK = "importXmlTask";
    public static final String BATCH_DEFINITION_ID = "definitionId";
    public static final String BATCH_DEFINITION = "definition";
}
