package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDetailRepository extends JpaRepository<EmployeeDetail, Integer> {
    public EmployeeDetail findEmployeeDetailByEmail(String email);
    public EmployeeDetail findEmployeeDetailByEmployeeUsername(String username);
    public EmployeeDetail findEmployeeDetailById(Integer id);
}
