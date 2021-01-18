package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.EngineType;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.converter.YearAttributeConverter;

import javax.persistence.*;
import java.time.Year;
import java.util.List;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "marque")
    private String marque;

    @Column(name = "model")
    private String model;

    @Column(name = "year")
    @Convert(converter = YearAttributeConverter.class)
    private Year year;

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type")
    private EngineType engineType;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "vin")
    private String vin;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "employee_car"
            , joinColumns = @JoinColumn(name = "car_id")
            , inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private List<Employee> employees;

    public Car() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

}
