package com.example.urlshortener;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortUrlResponse> shorten(@Valid @RequestBody ShortUrlRequest request) {
        String shortUrl = urlShortenerService.shorten(request.url());
        String code = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);
        return ResponseEntity.ok(new ShortUrlResponse(request.url(), shortUrl, code));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "url-shortener");
    }

    public record ShortUrlRequest(@NotBlank(message = "URL must not be blank") String url) {}

    public record ShortUrlResponse(String originalUrl, String shortUrl, String code) {}
}
