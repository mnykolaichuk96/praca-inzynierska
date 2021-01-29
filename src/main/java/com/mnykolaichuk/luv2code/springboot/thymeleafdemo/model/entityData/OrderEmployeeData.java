package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation.FieldMatch;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@FieldMatch.List({
        @FieldMatch(first = "password", second = "matchingPassword", message = " muszą byc jednakowe")
})
public class OrderEmployeeData implements Serializable {

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String description;

    @NotNull(message = "is required")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime creationDate;


    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String cityName;

    private Integer orderId;

    private List<OrderAnswerData> orderAnswerDataList;

    private CarData carData;

    public OrderEmployeeData() {
    }

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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public List<OrderAnswerData> getOrderAnswerDataList() {
        return orderAnswerDataList;
    }

    public void setOrderAnswerDataList(List<OrderAnswerData> orderAnswerDataList) {
        this.orderAnswerDataList = orderAnswerDataList;
    }

    public CarData getCarData() {
        return carData;
    }

    public void setCarData(CarData carData) {
        this.carData = carData;
    }
}
