package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class NullOrderAnswerForOrderException extends Exception {

    public NullOrderAnswerForOrderException() {
        super();
    }


    public NullOrderAnswerForOrderException(String message) {
        super(message);
    }


    public NullOrderAnswerForOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
