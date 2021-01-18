package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.InvalidTokenException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.WrapperString;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Employee;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeData;

public interface EmployeeService {
    public void register(EmployeeData employeeData)
            throws UserAlreadyExistException, EmailAlreadyExistException;
    public boolean verifyEmployee(String token) throws InvalidTokenException;
    public void update(EmployeeData employeeData, WrapperString wrapperString)
            throws UserAlreadyExistException, EmailAlreadyExistException;
    public EmployeeData getEmployeeDataByUsername(String username);
    public void deleteById(int id);
    public Employee findByUsername(String username);
    public boolean comparePassword(String password, String encodePassword);
    public Employee findById(Integer id);







}
