package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.Stan;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation.FieldMatch;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@FieldMatch.List({
        @FieldMatch(first = "password", second = "matchingPassword", message = " muszą byc jednakowe")
})
public class OrderAnswerData {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "is required")
    private LocalDate implementationDate;

    @Enumerated(EnumType.STRING)
    private Stan stan;

    private Integer OrderAnswerId;

    @NotNull(message = "is required")
    private Double price;

    private WorkshopData workshopData;

    public LocalDate getImplementationDate() {
        return implementationDate;
    }

    public void setImplementationDate(LocalDate implementationDate) {
        this.implementationDate = implementationDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Stan getStan() {
        return stan;
    }

    public void setStan(Stan stan) {
        this.stan = stan;
    }

    public Integer getOrderAnswerId() {
        return OrderAnswerId;
    }

    public void setOrderAnswerId(Integer orderAnswerId) {
        OrderAnswerId = orderAnswerId;
    }

    public WorkshopData getWorkshopData() {
        return workshopData;
    }

    public void setWorkshopData(WorkshopData workshopData) {
        this.workshopData = workshopData;
    }

    @Override
    public String toString() {
        return "OrderAnswerData{" +
                "implementationDate=" + implementationDate +
                ", stan=" + stan +
                ", OrderAnswerId=" + OrderAnswerId +
                ", price=" + price +
                ", workshopData=" + workshopData +
                '}';
    }
}
