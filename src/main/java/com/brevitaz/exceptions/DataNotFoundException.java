package com.brevitaz.exceptions;

public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException() {
        super();
    }
    public DataNotFoundException(String s) {
        super(s);
    }
    public DataNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public DataNotFoundException(Throwable throwable) {
        super(throwable);
    }
}