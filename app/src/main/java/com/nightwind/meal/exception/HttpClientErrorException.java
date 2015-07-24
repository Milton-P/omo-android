package com.nightwind.meal.exception;

/**
 * Created by nightwind on 15/4/11.
 */
public class HttpClientErrorException extends Exception{
    public HttpClientErrorException() {
    }
    public HttpClientErrorException(String msg) {
        super(msg);
    }
}
