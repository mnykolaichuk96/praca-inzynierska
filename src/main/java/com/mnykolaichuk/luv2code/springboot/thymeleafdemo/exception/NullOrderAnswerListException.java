package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class NullOrderAnswerListException extends Exception {

    public NullOrderAnswerListException() {
        super();
    }


    public NullOrderAnswerListException(String message) {
        super(message);
    }


    public NullOrderAnswerListException(String message, Throwable cause) {
        super(message, cause);
    }
}
