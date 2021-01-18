package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation.CompositeKey;

import javax.persistence.*;

@Entity
@Table(name = "employee_car")
@IdClass(CompositeKey.class)
public class EmployeeCar {

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @Id
    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "car_verified")
    private Integer carVerified;        // 0 - false; 1 - true; !0 - false

    public EmployeeCar() {
    }

    public EmployeeCar(Integer employeeId, Integer carId, Integer carVerified) {
        this.employeeId = employeeId;
        this.carId = carId;
        this.carVerified = carVerified;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Integer getCarVerified() {
        return carVerified;
    }

    public void setCarVerified(Integer accountVerified) {
            this.carVerified = accountVerified;
    }
}


