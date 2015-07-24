package com.nightwind.meal.exception;

/**
 * Created by nightwind on 15/4/11.
 */
public class HttpForbiddenException extends HttpClientErrorException{
    public HttpForbiddenException() {
    }
    public HttpForbiddenException(String msg) {
        super(msg);
    }
}
