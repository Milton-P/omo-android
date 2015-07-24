package com.nightwind.meal.exception;

/**
 * Created by nightwind on 15/4/11.
 */
public class NoLoginException extends Exception{
    public NoLoginException() {
    }
    public NoLoginException(String msg) {
        super(msg);
    }
}
