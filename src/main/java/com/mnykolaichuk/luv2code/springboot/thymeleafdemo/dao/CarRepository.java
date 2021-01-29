package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
public interface CarRepository extends JpaRepository<Car, Integer> {

    Car findCarById(int id);
    Car findCarByVinAndRegistrationNumberAndEmployeesIsNotNull(String vin, String registrationNumber);
    Car findCarByVinAndRegistrationNumberAndEmployeesIsNull(String vin, String registrationNumber);

    @Transactional
    @Modifying
    @Query("delete from Car c where c.id=:id")
    void deleteCarById(Integer id);
}
