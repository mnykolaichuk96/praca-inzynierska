package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.AbstractEmailContext;

import javax.mail.MessagingException;

public interface EmailService {
    void sendMail(final AbstractEmailContext email) throws MessagingException;
    void sendCarMail(final AbstractEmailContext email) throws MessagingException;
    public void sendInformationMail(AbstractEmailContext email) throws MessagingException;
}
