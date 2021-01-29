package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Order;

public class NoMoreOrderAnswerEmailContext extends AbstractEmailContext {

    private String token;


    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        Order order = (Order) context;
        put("order", order);
        setTo(order.getEmployeeDetail().getEmail());

        setTemplateLocation("email/null-order-answer");
        setSubject("No repeats from workshops");
        setFrom("no-reply@javadevjournal.com");
    }

    public void setInformationUrl(final String showDetailsUrl){
        put("showDetailsUrl", showDetailsUrl);
    }

}