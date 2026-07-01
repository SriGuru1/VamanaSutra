package com.urlShortener.Service;

import com.urlShortener.Exception.UrlNotFoundException;
import com.urlShortener.Model.Url;
import com.urlShortener.Repository.Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {

    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final Repo repo;

    private static final long OFFSET = 1000000L;

    @Autowired
    public UrlService(Repo repo){
        this.repo = repo;
    }

    public Url convert(String longUrl, String baseUrl) throws Exception {
        if (longUrl == null || longUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter Valid Url");
        }

        Url newUrl = new Url();
        newUrl.setLongUrl(longUrl);
        Url savedEntity = repo.save(newUrl);

        // Generate short code based on ID
        long professionalId = (savedEntity.getId() * 7919L) + OFFSET;
        String shortCode = encodeBase62(professionalId);
        
        savedEntity.setShortCode(shortCode);
        // Constructed dynamically from current request
        savedEntity.setShortUrl((baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") + "v/" + shortCode);
        
        return repo.save(savedEntity);
    }

    private String encodeBase62(long n) {
        if (n == 0) return String.valueOf(BASE62_CHARS.charAt(0));
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            sb.append(BASE62_CHARS.charAt((int) (n % 62)));
            n /= 62;
        }
        return sb.reverse().toString();
    }

    public Url findByShortUrl(String code) throws Exception {
        // Look up ONLY by code
        return repo.findByShortCode(code)
                .orElseThrow(() -> new UrlNotFoundException("Short Url Not Found"));
    }
}
