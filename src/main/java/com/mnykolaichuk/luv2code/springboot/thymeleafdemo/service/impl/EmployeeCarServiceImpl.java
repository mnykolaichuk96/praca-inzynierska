package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.EmployeeCarRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeCar;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmployeeCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeCarServiceImpl implements EmployeeCarService {

    @Autowired
    private EmployeeCarRepository employeeCarRepository;

    @Override
    public List<EmployeeCar> findAllByCarId(Integer carId) {
        try {
            return employeeCarRepository.findAllByCarId(carId);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public List<EmployeeCar> findAllByEmployeeId(Integer employeeId) {
        try {
            return employeeCarRepository.findAllByEmployeeId(employeeId);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public EmployeeCar findByEmployeeIdAndCarId(Integer employeeId, Integer carId) {
        try {
            return employeeCarRepository.findEmployeeCarByEmployeeIdAndCarId(employeeId, carId);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void delete(EmployeeCar employeeCar) {
        employeeCarRepository.deleteEmployeeCar(employeeCar);
    }

    @Override
    public void save(EmployeeCar employeeCar) {
        employeeCarRepository.save(employeeCar);
    }
}
