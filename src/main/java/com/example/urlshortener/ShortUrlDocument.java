package com.example.urlshortener;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "short_urls")
public record ShortUrlDocument(
        @Id
        String id,

        @Indexed(unique = true)
        String code,

        @Indexed(unique = true)
        String originalUrl
) {
    public ShortUrlDocument(String code, String originalUrl) {
        this(null, code, originalUrl);
    }
}
