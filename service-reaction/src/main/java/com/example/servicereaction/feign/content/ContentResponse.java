package com.example.servicereaction.feign.content;

import com.example.servicereaction.feign.TargetResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ContentResponse extends TargetResponse {
    private String id;
    private String name;
    private String photoUrl;

    public ContentResponse(String id) {
        this.id = id;
    }
}
