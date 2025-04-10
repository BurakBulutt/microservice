package com.example.serviceusers.users.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class Page<T> {
    private final List<T> content;
    @JsonProperty("page")
    private final PageUtil pageUtil;


    public <U> Page<U> map(Function<? super T,? extends U> mapper) {
        return new Page<>(this.content.stream().map(mapper).collect(Collectors.toList()),this.pageUtil);
    }

    public static <T> Page<T> emptyPage() {
        return new Page<>(Collections.emptyList(), PageUtil.unPaged());
    }
}
