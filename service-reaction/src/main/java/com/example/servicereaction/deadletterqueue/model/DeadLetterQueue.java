package com.example.servicereaction.deadletterqueue.model;

import com.example.servicereaction.deadletterqueue.enums.MessageType;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(DeadLetterQueue.TABLE)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class DeadLetterQueue {
    public static final String TABLE = "dead_letter_queue";

    @MongoId(FieldType.OBJECT_ID)
    private ObjectId id;

    private MessageType type;

    private String payload;
}
