package com.example.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UrlShortenerServiceTest {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Test
    void shortenShouldGenerateShortCodeForValidUrl() {
        String originalUrl = "https://example.com/products/123";

        String shortUrl = urlShortenerService.shorten(originalUrl);

        assertThat(shortUrl).startsWith("http://localhost:8080/go/");
        assertThat(shortUrl).matches("http://localhost:8080/go/[A-Za-z0-9]{6}");
    }

    @Test
    void resolveShouldReturnOriginalUrlForKnownCode() {
        String originalUrl = "https://example.com/hello";
        String shortUrl = urlShortenerService.shorten(originalUrl);
        String code = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);

        assertThat(urlShortenerService.resolve(code)).isEqualTo(originalUrl);
    }
}
