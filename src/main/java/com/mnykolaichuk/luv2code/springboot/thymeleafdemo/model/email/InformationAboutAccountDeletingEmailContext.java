package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Order;

public class InformationAboutAccountDeletingEmailContext extends AbstractEmailContext {

    @Override
    public <T> void init(T context1, T context2){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        Order order = (Order) context1;
        EmployeeDetail employeeDetail = (EmployeeDetail) context2;
        put("order", order);
        put("employeeDetail", employeeDetail);
        setTo(order.getOrderAnswers().get(0).getWorkshop().getEmail());
        setTemplateLocation("email/email-information-about-account-deleting");
        setSubject("Customer delete account");
        setFrom("no-reply@javadevjournal.com");
    }
}