package com.example.servicemedia.content.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageMapper {

    public static <T,U> Page<U> toPageDto(Page<T> page, Function<T, U> mapper) {
        return page.map(mapper);
    }
}
