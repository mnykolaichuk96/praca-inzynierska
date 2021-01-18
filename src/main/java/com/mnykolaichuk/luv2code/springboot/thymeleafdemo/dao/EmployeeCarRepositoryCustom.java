package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeCar;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeCarRepositoryCustom {
    public List<EmployeeCar> findEmployeeCarByCarId(int id);
}
