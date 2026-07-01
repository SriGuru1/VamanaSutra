package com.urlShortener.Controller;

import com.urlShortener.DTO.UrlRequest;
import com.urlShortener.Model.Url;
import com.urlShortener.Service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
public class ControllerMain {

    private final UrlService service;

    @Autowired
    public ControllerMain(UrlService service){
        this.service = service;
    }

    // Health check endpoint at /health to verify status without blocking the UI
    @GetMapping("/health")
    public ResponseEntity<Object> healthCheck() {
        return ResponseEntity.ok(Map.of("Status", "SHR.NK Service is Online", "Message", "App is running successfully!"));
    }

    // Shorten URL request
    @PostMapping("/api/url-shortener")
    public ResponseEntity<Object> urlShortener(@RequestBody UrlRequest longUrl, HttpServletRequest request){
        try{
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            Url url = service.convert(longUrl.getLongUrl(), baseUrl);
            return ResponseEntity.ok(url);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(Map.of("Error", e.getMessage()));
        }
    }

    // Short redirection path
    @GetMapping("/v/{shortCode}")
    public ResponseEntity<Object> redirectV(@PathVariable String shortCode){
        return findLongUrl(shortCode);
    }

    // Older /api/ redirect path
    @GetMapping("/api/{shortCode}")
    public ResponseEntity<Object> findLongUrl(@PathVariable String shortCode){
        try{
            Url url = service.findByShortUrl(shortCode);
            URI target = URI.create(url.getLongUrl());
            return ResponseEntity.status(HttpStatus.FOUND).location(target).build();
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", e.getMessage()));
        }
    }
}
