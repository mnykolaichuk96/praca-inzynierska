package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class tempException extends Exception {

    public tempException() {
        super();
    }


    public tempException(String message) {
        super(message);
    }


    public tempException(String message, Throwable cause) {
        super(message, cause);
    }
}
