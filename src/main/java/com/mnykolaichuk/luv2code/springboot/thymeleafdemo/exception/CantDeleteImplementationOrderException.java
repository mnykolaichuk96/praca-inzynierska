package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class CantDeleteImplementationOrderException extends Exception {

    public CantDeleteImplementationOrderException() {
        super();
    }


    public CantDeleteImplementationOrderException(String message) {
        super(message);
    }


    public CantDeleteImplementationOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
