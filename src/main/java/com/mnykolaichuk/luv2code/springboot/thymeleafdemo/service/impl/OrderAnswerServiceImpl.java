package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.OrderAnswerRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.CantDeleteWorkshopWhileImplementationExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.CompletedEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.ImplementationEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.NoMoreOrderAnswerEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.WorkshopAnswerEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Authority;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Order;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.OrderAnswer;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderAnswerData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.AuthorityEnum;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.Stan;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private EmployeeService employeeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Value("http://localhost:8080/employee/showOrderList")
    private String SHOW_ORDER_LIST_URL;

    @Value("http://localhost:8080/workshop/showImplementationOrderList")
    private String SHOW_IMPLEMENTATION_ORDER_LIST_URL;

    @Value("http://localhost:8080/employee/showCompletedOrderList")
    private String SHOW_COMPLETED_ORDER_LIST_URL;

    @Override
    public OrderAnswerData getOrderAnswerData(OrderAnswer orderAnswer) {
        OrderAnswerData orderAnswerData = new OrderAnswerData();
        BeanUtils.copyProperties(orderAnswer, orderAnswerData);
        orderAnswerData.setOrderAnswerId(orderAnswer.getId());
        if(isWorkshopInOrderAnswer(orderAnswer)) {
            orderAnswerData.setWorkshopData
                    (workshopService.getWorkshopDataByUsername(orderAnswer.getWorkshop().getUsername()));
        }
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

    @Override
    public OrderAnswer findById(Integer id) {
        try {
            return orderAnswerRepository.findOrderAnswerById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<OrderAnswer> findAllByWorkshopUsername(String username) {
        try {
            return orderAnswerRepository.findAllByWorkshopUsername(username);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void createWorkshopAnswerByOrderAnswerData(OrderAnswerData orderAnswerData) {
        OrderAnswer orderAnswer =
                orderAnswerRepository.findOrderAnswerById(orderAnswerData.getOrderAnswerId());
        orderAnswer.setImplementationDate(orderAnswerData.getImplementationDate());
        orderAnswer.setPrice(orderAnswerData.getPrice());
        orderAnswer.setStan(Stan.WORKSHOP_ANSWER);
        orderAnswerRepository.save(orderAnswer);
        sendStanChangeEmail(orderAnswer);
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
        sendStanChangeEmail(orderAnswer);
    }

    @Override
    public void chooseOrderAnswerForCompleted(OrderAnswer orderAnswer) {
        orderAnswer.setStan(Stan.COMPLETED);
        orderAnswerRepository.save(orderAnswer);
        for (OrderAnswer tempOrderAnswer : orderAnswer.getOrder().getOrderAnswers()) {
            if(tempOrderAnswer.getId() != orderAnswer.getId()) {
                orderAnswerRepository.deleteOrderAnswerById(tempOrderAnswer.getId());
            }
        }
        sendStanChangeEmail(orderAnswer);
    }
    private void sendStanChangeEmail(OrderAnswer orderAnswer) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                switch (orderAnswer.getStan()) {
                    case WORKSHOP_ANSWER:stanWorkshopAnswerEmail(orderAnswer);
                        break;
                    case IMPLEMENTATION:stanImplementationEmail(orderAnswer);
                        break;
                    case COMPLETED:stanCompletedEmail(orderAnswer);
                        break;
                }
            }
        });
    }
    private void stanWorkshopAnswerEmail(OrderAnswer orderAnswer) {
        WorkshopAnswerEmailContext emailContext = new WorkshopAnswerEmailContext();
        emailContext.init(orderAnswer);
        emailContext.setInformationUrl(SHOW_ORDER_LIST_URL);

        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void stanImplementationEmail(OrderAnswer orderAnswer) {
        ImplementationEmailContext emailContext = new ImplementationEmailContext();
        emailContext.init(orderAnswer);
        emailContext.setInformationUrl(SHOW_IMPLEMENTATION_ORDER_LIST_URL);

        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void stanCompletedEmail(OrderAnswer orderAnswer) {
        CompletedEmailContext emailContext = new CompletedEmailContext();
        emailContext.init(orderAnswer);
        emailContext.setInformationUrl(SHOW_COMPLETED_ORDER_LIST_URL);

        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(OrderAnswer orderAnswer) {
        orderAnswerRepository.save(orderAnswer);
    }

    @Override
    public void delete(OrderAnswer orderAnswer) {
        orderAnswerRepository.deleteOrderAnswerById(orderAnswer.getId());
    }

    @Override
    public void deleteOrderAnswerFromWorkshopByOrderAnswerAndWorkshopUsername(OrderAnswer orderAnswer, String username) throws CantDeleteWorkshopWhileImplementationExistException {
        if(orderAnswer.getStan() == Stan.CREATED || orderAnswer.getStan() == Stan.UNREGISTERED || orderAnswer.getStan() == Stan.WORKSHOP_ANSWER) {
            //jeżeli tylko aktualnie zalogowany warsztat ma to zlecenie
            if(orderAnswer.getOrder().getOrderAnswers().size() == 1) {
                sendNoMoreOrderAnswerEmail(orderAnswer.getOrder());
            }
            orderAnswer.setOrder(null);
            save(orderAnswer);
            delete(orderAnswer);
        }
        if(orderAnswer.getStan() == Stan.IMPLEMENTATION){
            Authority authority = new Authority();
            authority.setAuthority(AuthorityEnum.ROLE_ADMIN);
            if(employeeService.findByUsername(username) != null && employeeService.findByUsername(username).getAuthorities().contains(authority)) {
                Order order = orderAnswer.getOrder();

                orderAnswer.setOrder(null);
                save(orderAnswer);
                orderService.deleteOrderFromEmployeeByOrderAndEmployeeUsername(order,username);
                orderAnswerRepository.deleteOrderAnswerById(orderAnswer.getId());
            }
            else {
                throw new CantDeleteWorkshopWhileImplementationExistException("Zeby usunąć konto trzeba zakończyć wszystkie zlecenia");
            }
        }
        if(orderAnswer.getStan() == Stan.COMPLETED) {
            //jeżeli nie ma przypisanego EmployeeDetail usuwamy z bazy danych
            if(!orderService.isEmployeeDetailInOrder(orderAnswer.getOrder())) {
                Order order = orderAnswer.getOrder();
                orderAnswer.setOrder(null);
                save(orderAnswer);
                orderService.deleteOrderFromEmployeeByOrderAndEmployeeUsername(order, username);
                orderAnswerRepository.deleteOrderAnswerById(orderAnswer.getId());
            }
            else {
                orderAnswer.setWorkshop(null);
                save(orderAnswer);
            }
        }

    }

    private void sendNoMoreOrderAnswerEmail(Order order) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { noMoreOrderAnswerEmail(order);

            }
        });
    }

    private void noMoreOrderAnswerEmail(Order order) {
        NoMoreOrderAnswerEmailContext emailContext = new NoMoreOrderAnswerEmailContext();
        emailContext.init(order);
        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isWorkshopInOrderAnswer(OrderAnswer orderAnswer) {
        try {
            return orderAnswer.getWorkshop() != null ? true : false;
        } catch (NullPointerException e) {
            return false;
        }
    }
}


