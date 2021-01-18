package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeDetailData;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeDetailService {
    public EmployeeDetail findByUsername(String username);
    public EmployeeDetailData getEmployeeDetailData(EmployeeDetail employeeDetail);
}
