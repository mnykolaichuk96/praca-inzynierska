package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer>, OrderRepositoryCustom {
    @Transactional
    @Modifying
    @Query("delete from Order o where o.id=:id")
    public void deleteOrderById(Integer id);

    public List<Order> findAllByEmployeeDetail(EmployeeDetail employeeDetail);
    public Order findOrderById(Integer id);
}
