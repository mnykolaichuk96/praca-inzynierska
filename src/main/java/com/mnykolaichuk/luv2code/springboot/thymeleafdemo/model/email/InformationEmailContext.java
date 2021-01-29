package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;

public class InformationEmailContext extends AbstractEmailContext {

    private String token;


    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        if(isFromEmployeeDetail(context)) {
            EmployeeDetail employeeDetail = (EmployeeDetail) context; // we pass the customer informati
            put("firstName", employeeDetail.getFirstName() + " " + employeeDetail.getLastName());
            setTo(employeeDetail.getEmail());
        }
        else {
            Workshop workshop = (Workshop) context; // we pass the customer informati
            put("firstName", workshop.getWorkshopName());
            setTo(workshop.getEmail());
        }
        setTemplateLocation("email/email-verification");
        setSubject("Change order status");
        setFrom("no-reply@javadevjournal.com");
    }

    public void setInformationUrl(final String showDetailsUrl){
        put("showDetailsUrl", showDetailsUrl);
    }

}