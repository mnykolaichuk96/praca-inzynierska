package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    public List<Order> findAllByEmployeeDetail(EmployeeDetail employeeDetail);
    public Order findOrderById(Integer id);
}
