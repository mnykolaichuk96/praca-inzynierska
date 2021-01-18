package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.CarRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.EmployeeCarRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.EmployeeRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.InvalidTokenException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.MyCarAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.email.CarVerificationEmailContext;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.*;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.CarData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CarService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmailService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmployeeService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.SecureTokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    CarRepository carRepository;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeCarRepository employeeCarRepository;
    @Autowired
    SecureTokenService secureTokenService;
    @Autowired
    EmailService emailService;
    @Autowired
    EmployeeRepository employeeRepository;

    @Value("${site.base.url.http}")
    private String baseURL;

    private List<Employee> employees = new ArrayList<>();
    private List<Integer> carIds = null;

    @Override
    public List<Car> findAllCarByVin(String vin) {
        return carRepository.findAllCarByVinAndEmployeesIsNotNull(vin);
    }

    @Override
    public CarData getCarData(Car car) {
        CarData carData = new CarData();
        BeanUtils.copyProperties(car,carData);
        carData.setYear(car.getYear().toString());
        return carData;
    }

    @Override
    public void save(CarData carData) throws MyCarAlreadyExistException {
        Integer currentUserId = employeeService.findByUsername(carData.getUsername()).getId();
        boolean isVinExist = checkIfVinExist(carData.getVin());
        boolean carExist = false;
        int count = 1;
        if(isVinExist){
            //check if in db exist car with same vin and registry number
            for(Car car : findAllCarByVin(carData.getVin())){
                if(car.getRegistrationNumber().equals(carData.getRegistrationNumber())) {
                    carExist=true;
                    //знаходимо всіх клієнтів для машини з цим він номер і реєстраційним номером
                    for(EmployeeCar employeeCar : employeeCarRepository.findAllByCarId(car.getId())) {
                        if(employeeCar.getEmployeeId() == currentUserId) {
                            throw new MyCarAlreadyExistException("This car already exist in my cars list");
                        }
                        else {
                            //якщо цієї машини немає в лісті моїх тоді розсилаєм мейли власникам
                            employees.add(employeeService.findById(employeeCar.getEmployeeId()));
                            count++;
                        }
                    }
                    EmployeeCar employeeCar = new EmployeeCar(currentUserId, car.getId(), count);
                    for(Employee employee : employees){
                        sendCarConfirmationEmail(carData.getUsername(), employee.getEmployeeDetail(), car);
                    }
                    employeeCarRepository.save(employeeCar);
                }
            }
        }
        if(!carExist) {
            Car car = new Car();
            BeanUtils.copyProperties(carData, car);
            try {
                car.setYear(Year.of(Integer.parseInt(carData.getYear())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            car.setEmployees(Arrays.asList(employeeService.findByUsername(carData.getUsername())));

            carRepository.save(car);
            EmployeeCar employeeCar;
            employeeCar = employeeCarRepository.findEmployeeCarByEmployeeIdAndCarId
                    (currentUserId,
                            findCarIdByVinAndRegistrationNumber(car.getVin(), car.getRegistrationNumber()));
            employeeCar.setCarVerified(1);
            employeeCarRepository.save(employeeCar);
        }
    }

    @Override
    public Car saveForOrder(CarData carData) {
        List<Car> carsWithoutEmployees;
        carsWithoutEmployees = carRepository.findAllCarByVinAndEmployeesIsNull(carData.getVin());
        if(carsWithoutEmployees != null){
            for(Car car : carsWithoutEmployees) {
                if(car.getVin().equals(carData.getVin())) {
                    if (car.getRegistrationNumber().equals(carData.getRegistrationNumber())) {
                        return carRepository
                                .findCarByVinAndRegistrationNumberAndEmployeesIsNull
                                        (carData.getVin(), carData.getRegistrationNumber());
                    }
                }
            }
        }
        Car car = new Car();
        BeanUtils.copyProperties(carData, car);
        try {
            car.setYear(Year.of(Integer.parseInt(carData.getYear())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carRepository.save(car);

    }

    @Override
    public boolean checkIfVinExist(String vin) {
        return carRepository.findAllCarByVinAndEmployeesIsNotNull(vin) != null ? true : false;
    }

    @Override
    public Car findCarByVinAndRegistrationNumber(String vin, String registrationNumber) {
        return carRepository.findCarByVinAndRegistrationNumberAndEmployeesIsNotNull(vin, registrationNumber);
    }

    @Override
    public Integer findCarIdByVinAndRegistrationNumber(String vin, String registrationNumber) {
        return carRepository
                .findCarByVinAndRegistrationNumberAndEmployeesIsNotNull(vin, registrationNumber).getId();
    }

    @Override
    public List<Car> findAllCarsByUsername(String username) {
        List<Car> cars = new ArrayList<>();
        List<EmployeeCar> employeeCars =
                employeeCarRepository.findAllByEmployeeId(employeeService.findByUsername(username).getId());
        for(EmployeeCar employeeCar : employeeCars) {
            if(employeeCar.getCarVerified() == 1) {
                cars.add(carRepository.findCarById(employeeCar.getCarId()));
            }
        }

        return cars;
    }


    @Override
    public void sendCarConfirmationEmail(String fromEmployee, EmployeeDetail toEmployeeDetail, Car car) {
        SecureToken secureToken = secureTokenService.createSecureTokenForCar(car, fromEmployee);
        secureToken.setEmployeeDetail(toEmployeeDetail);
        secureTokenService.saveSecureToken(secureToken);
        CarVerificationEmailContext emailContext = new CarVerificationEmailContext();
        emailContext.init(toEmployeeDetail, car);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendCarMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyCar(String token) throws InvalidTokenException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if(Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()){
            throw new InvalidTokenException("Token is not valid");
        }
        Employee employee = employeeRepository.getOne(secureToken.getEmployeeDetail().getEmployee().getId());
        if(Objects.isNull(employee)){
            return false;
        }

        Integer carId = secureToken.getVerificationCarId();
        String username = secureToken.getVerificationUsername();
        EmployeeCar employeeCar =
                employeeCarRepository.findEmployeeCarByEmployeeIdAndCarId(employeeService.findByUsername(username).getId(), carId);
        employeeCar.setCarVerified(employeeCar.getCarVerified() - 1);
        employeeCarRepository.save(employeeCar); // let's same user details

        // we don't need invalid password now
        secureTokenService.removeToken(secureToken);
        return true;
    }
}
