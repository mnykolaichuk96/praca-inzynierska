package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.*;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.InformationEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Order;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.OrderAnswer;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderAnswerData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderEmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderWorkshopData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.Stan;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private EmployeeDetailRepository employeeDetailRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired
    private CarService carService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderAnswerRepository orderAnswerRepository;

    @Autowired
    private OrderAnswerService orderAnswerService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private EmployeeDetailService employeeDetailService;

    @Autowired
    private WorkshopService workshopService;

    private final String SHOW_DETAILS_URL="localhost:8080";

    @Override
    public void createOrder(String username, OrderEmployeeData orderEmployeeData) {
        Order order = new Order();
        order.setDescription(orderEmployeeData.getDescription());
        order.setCreationDate(orderEmployeeData.getCreationDate());
        order.setEmployeeDetail(employeeDetailRepository.findEmployeeDetailByEmployeeUsername(username));
        order.setCity(cityRepository.findCityByCityName(orderEmployeeData.getCityName()));
        order.setCar(carService.saveForOrder(orderEmployeeData.getCarData()));
        orderRepository.save(order);
        for(Workshop workshop : workshopRepository.findAllByCity(order.getCity())) {
            OrderAnswer tempOrderAnswer = new OrderAnswer();
            tempOrderAnswer.setStan(Stan.CREATED);
            tempOrderAnswer.setWorkshop(workshop);
            tempOrderAnswer.setOrder(order);
            orderAnswerRepository.save(tempOrderAnswer);
        }
        sendListInformationEmail(workshopRepository.findAllByCity(order.getCity()));
    }

    private void sendListInformationEmail(List<Workshop> workshops) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { informationEmail(workshops);

            }
        });
    }

    private void informationEmail(List<Workshop> workshops) {
        InformationEmailContext emailContext = new InformationEmailContext();
        for(Workshop workshop : workshops) {
            emailContext.init(workshop);
            emailContext.setInformationUrl(SHOW_DETAILS_URL);
            try {
                emailService.sendInformationMail(emailContext);
            }
            catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private OrderEmployeeData getOrderEmployeeDataByOrder(Order order) {
        OrderEmployeeData orderEmployeeData = new OrderEmployeeData();
        orderEmployeeData.setOrderId(order.getId());
        orderEmployeeData.setDescription(order.getDescription());
        orderEmployeeData.setCreationDate(order.getCreationDate());
        orderEmployeeData.setCityName(order.getCity().getCityName());
        orderEmployeeData.setOrderAnswerDataList
                (orderAnswerService.getOrderAnswerDataListByOrderAnswerList(order.getOrderAnswers()));
        orderEmployeeData.setCarData(carService.getCarData(order.getCar()));

        return orderEmployeeData;
    }

    @Override
    public List<OrderEmployeeData> getOrderEmployeeDataListByUsernameAndStanIsNotCompleted(String username) {
        return getOrderEmployeeDataListByUsernameAndStan(username, false);
    }

    @Override
    public List<OrderEmployeeData> getOrderEmployeeDataListByUsernameAndStanEqualsCompleted(String username) {
        return getOrderEmployeeDataListByUsernameAndStan(username, true);
    }

    private List<OrderEmployeeData>
    getOrderEmployeeDataListByUsernameAndStan(String username, boolean ifOrderCompleted) {
        EmployeeDetail employeeDetail =
                employeeDetailRepository.findEmployeeDetailByEmployeeUsername(username);
        List<Order> orders = orderRepository.findAllByEmployeeDetail(employeeDetail);
        List<OrderEmployeeData> orderEmployeeDataList = new ArrayList<>();
        for(Order order : orders) {
            if(!(ifOrderCompleted ^ checkIfOrderCompleted(order))) {
                orderEmployeeDataList.add(getOrderEmployeeDataByOrder(order));
            }
        }
        return orderEmployeeDataList;
    }

    private boolean checkIfOrderCompleted(Order order) {
        List<OrderAnswer> orderAnswers = order.getOrderAnswers();
        for(OrderAnswer orderAnswer : orderAnswers) {
            if(orderAnswer.getStan() == Stan.COMPLETED){
                return true;
            }
        }
        return false;
    }

    @Override
    public OrderEmployeeData getOrderEmployeeDataByOrderAndStanEqualsWorkshopAnswer(Order order) {
        OrderEmployeeData orderEmployeeData = getOrderEmployeeDataByOrder(order);
        List<OrderAnswerData> orderAnswerDataList = new ArrayList<>();
        for(OrderAnswerData orderAnswerData : orderEmployeeData.getOrderAnswerDataList()){
            if(orderAnswerData.getStan() == Stan.WORKSHOP_ANSWER) {
                orderAnswerDataList.add(orderAnswerData);
            }
        }
        orderEmployeeData.setOrderAnswerDataList(orderAnswerDataList);
        return orderEmployeeData;
    }

    //    private List<Order> findAllByOrderAnswers(List<OrderAnswer> orderAnswers) {
//        List<Order> tempOrders = new ArrayList<>();
//        for(OrderAnswer orderAnswer : orderAnswers) {
//            tempOrders.add(orderAnswer.getOrder());
//        }
//        return tempOrders;
//    }
//
//    private List<Order> findAllByUsernameAndStanEqualsCreated(String username) {
//        return findAllByOrderAnswers(orderAnswerService.findAllByUsernameAndStanEqualsCreated(username));
//    }
//
//    @Override
//    public List<OrderEmployeeData> getOrderDataListByUsernameAndStanEqualsCreated(String username) {
//        List<OrderEmployeeData> orderDataList = new ArrayList<>();
//        for(Order order : findAllByUsernameAndStanEqualsCreated(username)){
//            OrderEmployeeData tempOrderData = new OrderEmployeeData();
//            for(OrderAnswer orderAnswer : order.getOrderAnswers()) {
//                for(Workshop workshop : orderAnswer.getWorkshops()) {
//                    if(workshop.getUsername().equals(username)) {
//                        tempOrderData.setOrderAnswerId(orderAnswer.getId());
//                    }
//                }
//            }
//            tempOrderData.setDescription(order.getDescription());
//            tempOrderData.setCreationDate(order.getCreationDate());
//            tempOrderData.setCityName(order.getCity().getCityName());
//            tempOrderData.setEmployeeDetailData
//                    (employeeDetailService.getEmployeeDetailData(order.getEmployeeDetail()));
//            tempOrderData.setCarData(carService.getCarData(order.getCar()));
//            orderDataList.add(tempOrderData);
//        }
//        return orderDataList;
//    }
//
//    @Override
//    public List<OrderEmployeeData> getOrderDataListByUsernameAndStanEqualsWorkshopRepeat(String username) {
//        List<OrderEmployeeData> orderDataList = new ArrayList<>();
//        for(Order order : findAllByUsernameAndStanEqualsCreated(username)){
//            OrderEmployeeData tempOrderData = new OrderEmployeeData();
//            for(OrderAnswer orderAnswer : order.getOrderAnswers()) {
//                for(Workshop workshop : orderAnswer.getWorkshops()) {
//                    if(workshop.getUsername().equals(username)) {
//                        tempOrderData.setOrderAnswerId(orderAnswer.getId());
//                    }
//                }
//            }
//            tempOrderData.setDescription(order.getDescription());
//            tempOrderData.setCreationDate(order.getCreationDate());
//            tempOrderData.setCityName(order.getCity().getCityName());
//            tempOrderData.setEmployeeDetailData
//                    (employeeDetailService.getEmployeeDetailData(order.getEmployeeDetail()));
//            tempOrderData.setCarData(carService.getCarData(order.getCar()));
//            orderDataList.add(tempOrderData);
//        }
//        return orderDataList;
//    }
//
    @Override
    public Order findOrderById(Integer id) {
        return orderRepository.findOrderById(id);
    }

    @Override
    public List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsCreated(String username) {
        return getOrderWorkshopDataListByUsernameAndStan(username, Stan.CREATED);
    }

    @Override
    public List<OrderWorkshopData>
    getOrderWorkshopDataListByUsernameAndStanEqualsImplementation(String username) {
        return getOrderWorkshopDataListByUsernameAndStan(username, Stan.IMPLEMENTATION);
    }

    @Override
    public List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsCompleted(String username) {
        return getOrderWorkshopDataListByUsernameAndStan(username, Stan.COMPLETED);
    }

    private List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStan(String username, Stan stan) {
        List<OrderAnswer> orderAnswers = orderAnswerRepository.findAllByWorkshopUsername(username);
        List<OrderWorkshopData> orderWorkshopDataList = new ArrayList<>();
        OrderWorkshopData orderWorkshopData;
        for(OrderAnswer orderAnswer : orderAnswers) {
            if(orderAnswer.getStan() == stan) {
                orderWorkshopData = new OrderWorkshopData();
                orderWorkshopData.setOrderAnswerId(orderAnswer.getId());
                orderWorkshopData.setDescription(orderAnswer.getOrder().getDescription());
                orderWorkshopData.setCreationDate(orderAnswer.getOrder().getCreationDate());
                orderWorkshopData.setCityName(orderAnswer.getOrder().getCity().getCityName());
                orderWorkshopData.setCarData(carService.getCarData(orderAnswer.getOrder().getCar()));
                orderWorkshopData.setEmployeeDetailData(employeeDetailService
                        .getEmployeeDetailData(orderAnswer.getOrder().getEmployeeDetail()));
                orderWorkshopData.setOrderAnswerData(orderAnswerService.getOrderAnswerData(orderAnswer));

                orderWorkshopDataList.add(orderWorkshopData);
            }
        }
        return orderWorkshopDataList;
    }


}
