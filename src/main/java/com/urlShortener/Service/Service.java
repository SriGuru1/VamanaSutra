package com.urlShortener.Service;

import com.urlShortener.Exception.UrlNotFoundException;
import com.urlShortener.Model.Url;
import com.urlShortener.Repository.Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final Repo repo;

    @Value("${app.baseUrl}")
    private String baseUrl;

    // Every link gets a professional starting point (Offset)
    private static final long OFFSET = 1000000L;

    @Autowired
    public Service(Repo repo){
        this.repo = repo;
    }

    public Url convert(String longUrl) throws Exception {
        if (longUrl == null || longUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter Valid Url");
        }

        Url newUrl = new Url();
        newUrl.setLongUrl(longUrl);
        Url savedEntity = repo.save(newUrl);

        // Professional Shuffle: Make ID look random but keep it unique
        long professionalId = (savedEntity.getId() * 7919L) + OFFSET;
        String shortId = encodeBase62(professionalId);
        
        // Clean Prefix: '/v/' looks much better than '/api/'
        savedEntity.setShortUrl(baseUrl + "/v/" + shortId);
        repo.save(savedEntity);

        return savedEntity;
    }

    private String encodeBase62(long n) {
        if (n == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            sb.append(BASE62_CHARS.charAt((int) (n % 62)));
            n /= 62;
        }
        return sb.reverse().toString();
    }

    public Url findByShortUrl(String code) throws Exception {
        // Look for the code in both new (/v/) and old (/api/) formats for compatibility
        String vFormat = baseUrl + "/v/" + code;
        String apiFormat = baseUrl + "/api/" + code;
        
        Optional<Url> url = repo.findByShortUrl(vFormat);
        if (url.isEmpty()) {
            url = repo.findByShortUrl(apiFormat);
        }
        
        return url.orElseThrow(() -> new UrlNotFoundException("Short Url Not Found"));
    }
}
