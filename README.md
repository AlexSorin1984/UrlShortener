# URL Shortener Microservice

A simple Spring Boot microservice that accepts a long URL and returns a short URL like TinyURL/Bitly.

## Features

- POST `/api/shorten` accepts a JSON body like:
  ```json
  {
    "url": "https://example.com/products/123"
  }
  ```
- Response:
  ```json
  {
    "originalUrl": "https://example.com/products/123",
    "shortUrl": "http://localhost:8080/go/AbCd12",
    "code": "AbCd12"
  }
  ```
- GET `/go/{code}` redirects to the original URL.
- GET `/api/health` returns service health status.

## Run locally

```bash
cd urlshortener
mvn spring-boot:run
```

## Example request

```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/products/123"}'
```

## Example redirect

```bash
curl -I http://localhost:8080/go/AbCd12
```
