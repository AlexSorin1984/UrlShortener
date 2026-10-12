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

    private final Map<String, String> longToShort = new ConcurrentHashMap<>();
    private final Map<String, String> shortToLong = new ConcurrentHashMap<>();

    public String shorten(String originalUrl) {
        String normalizedUrl = normalizeUrl(originalUrl);

        return longToShort.computeIfAbsent(normalizedUrl, url -> {
            String code = generateCode();
            while (shortToLong.containsKey(code)) {
                code = generateCode();
            }
            shortToLong.put(code, url);
            return BASE_URL + code;
        });
    }

    public String resolve(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Short code must not be blank");
        }

        String originalUrl = shortToLong.get(code);
        if (originalUrl == null) {
            throw new IllegalArgumentException("Short code does not exist");
        }
        return originalUrl;
    }

    public int count() {
        return shortToLong.size();
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
