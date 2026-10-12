package com.example.urlshortener;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlShortenerService {

    private static final String BASE_URL = "http://localhost:8080/go/";
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ShortUrlRepository shortUrlRepository;
    private final Map<String, String> longToShort = new ConcurrentHashMap<>();
    private final Map<String, String> shortToLong = new ConcurrentHashMap<>();

    public UrlShortenerService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    public String shorten(String originalUrl) {
        String normalizedUrl = normalizeUrl(originalUrl);

        String existingShortUrl = longToShort.get(normalizedUrl);
        if (existingShortUrl != null) {
            return existingShortUrl;
        }

        try {
            String existingCode = shortUrlRepository.findByOriginalUrl(normalizedUrl)
                    .map(ShortUrlDocument::code)
                    .orElse(null);
            if (existingCode != null) {
                String shortUrl = BASE_URL + existingCode;
                longToShort.putIfAbsent(normalizedUrl, shortUrl);
                shortToLong.putIfAbsent(existingCode, normalizedUrl);
                return shortUrl;
            }
        } catch (Exception ignored) {
            // Fall back to in-memory storage if the MongoDB Atlas cluster is unavailable.
        }

        String code = generateCode();
        while (shortToLong.containsKey(code) || repositoryContainsCode(code)) {
            code = generateCode();
        }

        String shortUrl = BASE_URL + code;
        try {
            shortUrlRepository.save(new ShortUrlDocument(code, normalizedUrl));
        } catch (Exception ignored) {
            // Fall back to local memory when MongoDB is not reachable.
        }

        shortToLong.putIfAbsent(code, normalizedUrl);
        longToShort.putIfAbsent(normalizedUrl, shortUrl);
        return shortUrl;
    }

    public String resolve(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Short code must not be blank");
        }

        String originalUrl = shortToLong.get(code);
        if (originalUrl != null) {
            return originalUrl;
        }

        try {
            return shortUrlRepository.findByCode(code)
                    .map(ShortUrlDocument::originalUrl)
                    .orElseThrow(() -> new IllegalArgumentException("Short code does not exist"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Short code does not exist", e);
        }
    }

    public int count() {
        try {
            return (int) shortUrlRepository.count();
        } catch (Exception ignored) {
            return shortToLong.size();
        }
    }

    private boolean repositoryContainsCode(String code) {
        try {
            return shortUrlRepository.existsByCode(code);
        } catch (Exception ignored) {
            return false;
        }
    }

    private String normalizeUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new IllegalArgumentException("URL must not be empty");
        }

        String trimmed = originalUrl.trim();
        try {
            URI uri = new URI(trimmed);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new IllegalArgumentException("URL must include a valid scheme and host");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL is not valid", e);
        }

        return trimmed;
    }

    private String generateCode() {
        StringBuilder builder = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }
}
