package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.AccountVerificationEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.SecureToken;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public abstract class AbstractService {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private EmailService emailService;

    @Value("${site.base.url.http}")
    private String baseURL;

    protected <T> void sendRegistrationConfirmationEmail(T data) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { registrationConfirmationEmail(data);

            }
        });
    }

    private <T> void registrationConfirmationEmail(T data) {
        SecureToken secureToken = secureTokenService.createSecureToken();
        if(data.getClass().getName().equals(EmployeeDetail.class.getName())) {
            secureToken.setEmployeeDetail((EmployeeDetail) data);
        }
        else {
            secureToken.setWorkshop((Workshop) data);
        }
        secureTokenService.saveSecureToken(secureToken);
        AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext();
        emailContext.init(data);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
