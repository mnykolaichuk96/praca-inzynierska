package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.EmployeeDetailRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeDetailData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmployeeDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDetailServiceImpl implements EmployeeDetailService {

    @Autowired
    private EmployeeDetailRepository employeeDetailRepository;

    @Override
    public EmployeeDetail findByUsername(String username) {
        return employeeDetailRepository.findEmployeeDetailByEmployeeUsername(username);
    }

    @Override
    public EmployeeDetailData getEmployeeDetailData(EmployeeDetail employeeDetail) {
        EmployeeDetailData employeeDetailData = new EmployeeDetailData();
        BeanUtils.copyProperties(employeeDetail, employeeDetailData);
        return employeeDetailData;
    }
}
