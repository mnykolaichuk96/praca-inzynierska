package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.EmployeeCarRepositoryCustom;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeCar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
@Repository
public class EmployeeCarRepositoryCustomImpl implements EmployeeCarRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<EmployeeCar> findEmployeeCarByCarId(int id) {
        Query query = entityManager.createQuery("SELECT ec from EmployeeCar ec " +
                "where ec.carId =: carId");
        query.setParameter("carId", id);
        List<EmployeeCar> employeeCars = null;

        try {
            employeeCars = query.getResultList();
        } catch (Exception e) {
            employeeCars = null;
            e.printStackTrace();
        }
        return employeeCars;
    }

}

