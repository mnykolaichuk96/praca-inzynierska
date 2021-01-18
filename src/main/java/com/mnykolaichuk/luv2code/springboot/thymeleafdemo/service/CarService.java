package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.InvalidTokenException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.MyCarAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Car;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.CarData;

import java.util.List;
public interface CarService {
    public Car findCarByVinAndRegistrationNumber(String vin, String registrationNumber);
    public Integer findCarIdByVinAndRegistrationNumber(String vin, String registrationNumber);
    public List<Car> findAllCarByVin(String vin);
    public CarData getCarData(Car car);
    public void save(CarData carData) throws MyCarAlreadyExistException;
    public Car saveForOrder(CarData carData);
    public boolean checkIfVinExist(String vin);
    public void sendCarConfirmationEmail(String fromEmployee, EmployeeDetail toEmployeeDetail, Car car);
    public boolean verifyCar(String token) throws InvalidTokenException;
    public List<Car> findAllCarsByUsername(String userName);
}
