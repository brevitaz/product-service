package com.brevitaz.exceptions;

public class ValidationException extends RuntimeException{
    public ValidationException(){}
    public ValidationException(String message){
        super(message);
    }

}
