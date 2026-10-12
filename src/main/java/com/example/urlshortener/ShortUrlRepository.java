package com.example.urlshortener;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShortUrlRepository extends MongoRepository<ShortUrlDocument, String> {
    Optional<ShortUrlDocument> findByCode(String code);

    Optional<ShortUrlDocument> findByOriginalUrl(String originalUrl);

    boolean existsByCode(String code);
}
