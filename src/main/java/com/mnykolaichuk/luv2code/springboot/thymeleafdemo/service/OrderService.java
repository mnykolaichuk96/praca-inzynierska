package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Order;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderEmployeeData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderWorkshopData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    public void createOrder(String username, OrderEmployeeData orderData);
    public List<OrderEmployeeData> getOrderEmployeeDataListByUsernameAndStanIsNotCompleted(String username);
    public OrderEmployeeData getOrderEmployeeDataByOrderAndStanEqualsWorkshopAnswer(Order order);
//    public List<Order> findAllByOrderAnswers(List<OrderAnswer> orderAnswers);
//    public List<Order> findAllByUsernameAndStanEqualsCreated(String username);
//    public List<OrderEmployeeData> getOrderDataListByUsernameAndStanEqualsCreated(String username);
//    public List<OrderEmployeeData> getOrderDataListByUsernameAndStanEqualsWorkshopRepeat(String username);
    public Order findOrderById(Integer id);

    public List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsCreated(String username);
    public List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsImplementation(String username);
    public List<OrderEmployeeData> getOrderEmployeeDataListByUsernameAndStanEqualsCompleted(String username);
    public List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsCompleted(String username);






}
