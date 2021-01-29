package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class OrderWorkshopData {
    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String description;

    @NotNull(message = "is required")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime creationDate;

    @NotNull(message = "is required")
    private String cityName;

    private Integer orderAnswerId;

    private OrderAnswerData orderAnswerData;

    private EmployeeDetailData employeeDetailData;

    private CarData carData;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getOrderAnswerId() {
        return orderAnswerId;
    }

    public void setOrderAnswerId(Integer orderAnswerId) {
        this.orderAnswerId = orderAnswerId;
    }

    public OrderAnswerData getOrderAnswerData() {
        return orderAnswerData;
    }

    public void setOrderAnswerData(OrderAnswerData orderAnswerData) {
        this.orderAnswerData = orderAnswerData;
    }

    public EmployeeDetailData getEmployeeDetailData() {
        return employeeDetailData;
    }

    public void setEmployeeDetailData(EmployeeDetailData employeeDetailData) {
        this.employeeDetailData = employeeDetailData;
    }

    public CarData getCarData() {
        return carData;
    }

    public void setCarData(CarData carData) {
        this.carData = carData;
    }

    @Override
    public String toString() {
        return "OrderWorkshopData{" +
                "description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", cityName='" + cityName + '\'' +
                ", orderAnswerId=" + orderAnswerId +
                ", orderAnswerData=" + orderAnswerData +
                ", employeeDetailData=" + employeeDetailData +
                ", carData=" + carData +
                '}';
    }
}
