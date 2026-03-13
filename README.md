# URL Shortener

A simple URL shortener service built with Spring Boot. It takes a long URL and returns a shorter, unique URL. When the short URL is accessed, it redirects the user to the original long URL.

## Features

*   Shorten long URLs.
*   Redirect short URLs to the original long URL.
*   Custom exception handling.

## Getting Started

### Prerequisites

*   Java 17 or later
*   Maven
*   A running instance of a database (The current implementation uses H2 in-memory, but it can be configured for other databases like MySQL or PostgreSQL).

### Installation

1.  Clone the repository.
2.  Navigate to the project directory.
3.  Run `mvn spring-boot:run`.
4.  The application will be running on `http://localhost:8080`.

## API Endpoints

### Create a short URL

*   **URL:** `/api/url-shortener`
*   **Method:** `POST`
*   **Body:**
    ```json
    {
        "longUrl": "https://www.example.com/a/very/long/url"
    }
    ```
*   **Success Response:**
    *   **Code:** 200 OK
    *   **Content:**
        ```json
        {
            "id": 1,
            "longUrl": "https://www.example.com/a/very/long/url",
            "shortUrl": "http://localhost:8080/api/1"
        }
        ```
*   **Error Response:**
    *   **Code:** 400 Bad Request
    *   **Content:**
        ```json
        {
            "Error": "Enter Valid Url"
        }
        ```

### Redirect to long URL

*   **URL:** `/api/{shortUrl}`
*   **Method:** `GET`
*   **URL Params:** `shortUrl=[the-short-url-code]` (e.g., `1`)
*   **Success Response:**
    *   **Code:** 302 Found
    *   **Redirects to:** The original long URL.
*   **Error Response:**
    *   **Code:** 404 Not Found
    *   **Content:**
        ```json
        {
            "Error": "Short Url Not Found"
        }
        ```

## Technologies Used

*   Java 17
*   Spring Boot
*   Spring Web
*   Spring Data JPA
*   H2 Database (in-memory)
*   Maven
