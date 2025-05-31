package com.example.servicereaction.feign.media;

import com.example.servicereaction.feign.TargetResponse;
import com.example.servicereaction.feign.content.ContentResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse extends TargetResponse {
    private String id;
    private String name;
    private ContentResponse content;

    public MediaResponse(String id) {
        this.id = id;
    }
}
