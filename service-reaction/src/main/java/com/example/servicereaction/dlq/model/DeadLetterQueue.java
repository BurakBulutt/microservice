package com.example.servicereaction.dlq.model;

import com.example.servicereaction.dlq.enums.MessageType;
import com.example.servicereaction.util.persistance.AbstractEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity(name = DeadLetterQueue.TABLE)
@Getter
@Setter
@NoArgsConstructor
public class DeadLetterQueue extends AbstractEntity {
    public static final String TABLE = "dlq";

    private MessageType type;

    private String payload;
}
