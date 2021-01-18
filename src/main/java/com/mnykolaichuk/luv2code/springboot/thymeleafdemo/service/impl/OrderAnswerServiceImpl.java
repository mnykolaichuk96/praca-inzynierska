package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.OrderAnswerRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.WorkshopRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.InformationEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.OrderAnswer;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderAnswerData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.Stan;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmailService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.OrderAnswerService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.WorkshopService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderAnswerServiceImpl implements OrderAnswerService {

    @Autowired
    private OrderAnswerRepository orderAnswerRepository;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private WorkshopRepository workshopRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TaskExecutor taskExecutor;

    private final String SHOW_DETAILS_URL="localhost:8080";

    @Override
    public OrderAnswerData getOrderAnswerData(OrderAnswer orderAnswer) {
        OrderAnswerData orderAnswerData = new OrderAnswerData();
        BeanUtils.copyProperties(orderAnswer, orderAnswerData);
        orderAnswerData.setOrderAnswerId(orderAnswer.getId());
        orderAnswerData.setWorkshopData
                (workshopService.getWorkshopDataByUsername(orderAnswer.getWorkshop().getUsername()));
        return orderAnswerData;
    }

    @Override
    public List<OrderAnswerData> getOrderAnswerDataListByOrderAnswerList(List<OrderAnswer> orderAnswerList) {
        List<OrderAnswerData> orderAnswerDataList = new ArrayList<>();
        for(OrderAnswer orderAnswer : orderAnswerList) {
            orderAnswerDataList.add(getOrderAnswerData(orderAnswer));
        }
        return orderAnswerDataList;
    }



    //    @Override
//    public List<OrderAnswer> findAllByWorkshopAndStanEquals(String username, Stan stan) {
//        List<OrderAnswer> orderAnswersWithStanEquals = new ArrayList<>();
//        if(workshopRepository.findWorkshopByUsername(username) != null) {
//            List<OrderAnswer> orderAnswers =
//                    orderAnswerRepository.findAllOrderAnswerByWorkshops
//                            (workshopRepository.findWorkshopByUsername(username));
//        }
//        else {
//            List<OrderAnswer> orderAnswers =
//                    orderAnswerRepository.findAllOrderAnswerByWorkshops
//                            (workshopRepository.findWorkshopByUsername(username));
//        }
//        for (OrderAnswer orderAnswer : orderAnswers) {
//            if(orderAnswer.getStan() == stan){
//                orderAnswersWithStanEquals.add(orderAnswer);
//            }
//        }
//        return orderAnswersWithStanEquals;
//    }
//
//    @Override
//    public List<OrderAnswer> findAllByUsernameAndStanEqualsCreated(String username) {
//        return findAllByWorkshopAndStanEquals(workshopService.findByUsername(username), Stan.CREATED);
//    }
//


    @Override
    public OrderAnswer findById(Integer id) {
        return orderAnswerRepository.findOrderAnswerById(id);
    }

    @Override
    public void createWorkshopAnswerByOrderAnswerData(OrderAnswerData orderAnswerData) {
        OrderAnswer orderAnswer =
                orderAnswerRepository.findOrderAnswerById(orderAnswerData.getOrderAnswerId());
        orderAnswer.setImplementationDate(orderAnswerData.getImplementationDate());
        orderAnswer.setPrice(orderAnswerData.getPrice());
        orderAnswer.setStan(Stan.WORKSHOP_ANSWER);
        orderAnswerRepository.save(orderAnswer);
        sendInformationEmail(orderAnswer);
    }

    @Override
    public void chooseOrderAnswerForImplementation(OrderAnswer orderAnswer) {
        orderAnswer.setStan(Stan.IMPLEMENTATION);
        orderAnswerRepository.save(orderAnswer);
        for(OrderAnswer tempOrderAnswer : orderAnswer.getOrder().getOrderAnswers()){
            if(tempOrderAnswer.getId() != orderAnswer.getId()) {
                orderAnswerRepository.deleteOrderAnswerById(tempOrderAnswer.getId());
            }
        }
        sendInformationEmail(orderAnswer);
    }

    @Override
    public void chooseOrderAnswerForCompleted(OrderAnswer orderAnswer) {
        orderAnswer.setStan(Stan.COMPLETED);
        orderAnswerRepository.save(orderAnswer);
        sendInformationEmail(orderAnswer);
    }

    private void sendInformationEmail(OrderAnswer orderAnswer) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { informationEmail(orderAnswer);

            }
        });
    }

    private void informationEmail(OrderAnswer orderAnswer) {
        InformationEmailContext emailContext = new InformationEmailContext();
        if(orderAnswer.getStan() == Stan.WORKSHOP_ANSWER || orderAnswer.getStan() == Stan.CREATED) {
            emailContext.init(orderAnswer.getOrder().getEmployeeDetail());
            emailContext.setInformationUrl(SHOW_DETAILS_URL);
        }
        else {
            emailContext.init(orderAnswer.getWorkshop());
            emailContext.setInformationUrl(SHOW_DETAILS_URL);
        }
        try {
            emailService.sendInformationMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
//
//    @Override
//    public void sendInformationEmail(Integer orderAnswerId) {
//        EmployeeDetail employeeDetail =
//                orderAnswerRepository.findOrderAnswerById(orderAnswerId).getOrder().getEmployeeDetail();
//        InformationEmailContext emailContext = new InformationEmailContext();
//        emailContext.init(employeeDetail);
//        emailContext.setInformationUrl(SHOW_DETAILS_URL);
//        try {
//            emailService.sendInformationMail(emailContext);
//        }
//        catch (MessagingException e) {
//            e.printStackTrace();
//        }
//
//    }

