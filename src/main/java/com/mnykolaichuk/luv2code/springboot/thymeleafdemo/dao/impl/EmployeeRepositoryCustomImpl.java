package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.EmployeeRepositoryCustom;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {
    //place for implementation of custom DAO methods

//    @Override
//    @Transactional
//    public List<Car> findAllCarsByUserName(String userName) {
//        Session currentSession = entityManager.unwrap(Session.class);
//
//        // now retrieve/read from database using name
//        Query<Car> theQuery = currentSession.createQuery("SELECT c from EmployeeCar ec, Employee e join e.cars c" +
//                " where e.username =: userName");
//
//        theQuery.setParameter("userName", userName);
//
//        List<Car> cars = null;
//
//        try {
//            cars = theQuery.getResultList();
//        } catch (Exception e) {
//            cars = null;
//        }
//        return cars;
//    }

}

