package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext {

    private String token;


    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        if(isFromEmployeeDetail(context)) {
            EmployeeDetail employeeDetail = (EmployeeDetail) context; // we pass the customer informati
            put("name", employeeDetail.getFirstName() + " " + employeeDetail.getLastName());
            setTo(employeeDetail.getEmail());
        }
        else {
            Workshop workshop = (Workshop) context; // we pass the customer informati
            put("name", workshop.getWorkshopName());
            setTo(workshop.getEmail());
        }
        // here we set thymeleaf .html for view mail
        setTemplateLocation("email/email-verification");
        setSubject("Verify your email");
        setFrom("no-reply@javadevjournal.com");
    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token){
        final String url= UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("/verify/user").queryParam("token", token).toUriString();
        put("verificationURL", url);
    }
}