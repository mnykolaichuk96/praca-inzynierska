package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.EmployeeCarRepositoryCustom;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeCar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
@Repository
public class EmployeeCarRepositoryCustomImpl implements EmployeeCarRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    @Modifying
    @Override
    public void deleteEmployeeCar(EmployeeCar employeeCar) {
        Query query = entityManager.createQuery("delete from EmployeeCar ec where ec.carId=:carId and ec.employeeId=:employeeId");
        query.setParameter("carId", employeeCar.getCarId());
        query.setParameter("employeeId", employeeCar.getEmployeeId());

        try {
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

