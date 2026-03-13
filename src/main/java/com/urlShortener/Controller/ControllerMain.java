package com.urlShortener.Controller;

import com.urlShortener.DTO.UrlRequest;
import com.urlShortener.Exception.IllegalArgumentException;
import com.urlShortener.Exception.UrlNotFoundException;
import com.urlShortener.Model.Url;
import com.urlShortener.Service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ControllerMain {

    private final Service service;

    @Autowired
    public ControllerMain(Service service){
        this.service = service;
    }

    @PostMapping("url-shortener")
    public ResponseEntity<Object> urlShortener(@RequestBody UrlRequest longUrl){
        try{
            Url url = service.convert(longUrl.getLongUrl());
            return ResponseEntity.ok(url);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(Map.of("Error",e.getMessage()));
        }
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Object> findLongUrl(@PathVariable String shortUrl){
        try{
            Url url = service.findByShortUrl(shortUrl);
            URI target = URI.create(url.getLongUrl());
            return ResponseEntity.status(HttpStatus.FOUND).location(target).build();
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", e.getMessage()));
        }
    }
}
