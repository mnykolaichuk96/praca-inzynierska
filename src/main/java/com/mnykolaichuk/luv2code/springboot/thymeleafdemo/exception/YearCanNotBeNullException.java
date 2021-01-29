package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class YearCanNotBeNullException extends Exception {

    public YearCanNotBeNullException() {
        super();
    }


    public YearCanNotBeNullException(String message) {
        super(message);
    }


    public YearCanNotBeNullException(String message, Throwable cause) {
        super(message, cause);
    }
}
