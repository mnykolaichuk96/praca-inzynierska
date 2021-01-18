package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.OrderAnswer;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.OrderAnswerData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderAnswerService {
    public OrderAnswerData getOrderAnswerData(OrderAnswer orderAnswer);
    public List<OrderAnswerData> getOrderAnswerDataListByOrderAnswerList(List<OrderAnswer> orderAnswerList);
//    public List<OrderAnswer> findAllByWorkshopAndStanEquals(String username, Stan stan);
//    public List<OrderAnswer> findAllByUsernameAndStanEqualsCreated(String username);
    public void createWorkshopAnswerByOrderAnswerData(OrderAnswerData orderAnswerData);
    public void chooseOrderAnswerForImplementation(OrderAnswer orderAnswer);
    public void chooseOrderAnswerForCompleted(OrderAnswer orderAnswer);
    public OrderAnswer findById(Integer id);

//    public void sendInformationEmail(Integer orderAnswerId);
//    public void createImplementationOrderAnswer(OrderAnswerData orderAnswerData);
}
