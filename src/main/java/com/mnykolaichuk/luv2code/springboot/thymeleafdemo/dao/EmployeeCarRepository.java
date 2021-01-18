package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeCarRepository extends JpaRepository<EmployeeCar, Integer>, EmployeeCarRepositoryCustom{
    public List<EmployeeCar> findAllByCarId(Integer carId);
    public List<EmployeeCar> findAllByEmployeeId(Integer employeeId);
    public EmployeeCar findEmployeeCarByEmployeeIdAndCarId(Integer employeeId, Integer carId);
}
