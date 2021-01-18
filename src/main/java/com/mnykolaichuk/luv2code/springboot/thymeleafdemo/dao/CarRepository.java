package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CarRepository extends JpaRepository<Car, Integer> {

    public Car findCarById(int id);
    public Car findCarByVinAndRegistrationNumberAndEmployeesIsNotNull(String vin, String registrationNumber);
    public List<Car> findAllCarByVinAndEmployeesIsNotNull(String vin);
    public List<Car> findAllCarByVinAndEmployeesIsNull(String vin);
    public Car findCarByVinAndRegistrationNumberAndEmployeesIsNull(String vin, String registrationNumber);
}
