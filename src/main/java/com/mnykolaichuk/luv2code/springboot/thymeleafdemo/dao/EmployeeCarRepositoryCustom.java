package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeCar;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeCarRepositoryCustom {
    public void deleteEmployeeCar(EmployeeCar employeeCar);
}
