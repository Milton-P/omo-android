package com.nightwind.meal.exception;

/**
 * Created by nightwind on 15/4/11.
 */
public class HttpBadRequestException extends HttpClientErrorException{
    public HttpBadRequestException() {
    }
    public HttpBadRequestException(String msg) {
        super(msg);
    }
}
