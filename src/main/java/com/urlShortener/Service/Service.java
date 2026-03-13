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

    @Autowired
    public Service(Repo repo){
        this.repo = repo;
    }

    public Url convert(String longUrl) throws Exception {
        if (longUrl == null || longUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter Valid Url");
        }

        Optional<Url> byLongUrl = repo.findByLongUrl(longUrl);
        if(byLongUrl.isPresent()){
            return byLongUrl.get();
        }

        Url newUrl = new Url();
        newUrl.setLongUrl(longUrl);
        Url savedEntity = repo.save(newUrl);

        String shortId = encodeBase62(savedEntity.getId());
        savedEntity.setShortUrl(baseUrl+"/api/" + shortId);
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

    public Url findByShortUrl(String shortUrl) throws  Exception{
        String fullShortUrl = baseUrl+"/api/" + shortUrl;
        Optional<Url> url = repo.findByShortUrl(fullShortUrl);
        Url foundUrl = url.orElseThrow(()-> new UrlNotFoundException("Short Url Not Found"));

        return foundUrl;
    }
}
