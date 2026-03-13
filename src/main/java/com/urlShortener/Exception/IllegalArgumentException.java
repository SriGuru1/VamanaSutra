package com.urlShortener.Exception;

public class IllegalArgumentException extends RuntimeException {
    public IllegalArgumentException(String message){
        super(message);
    }
}
