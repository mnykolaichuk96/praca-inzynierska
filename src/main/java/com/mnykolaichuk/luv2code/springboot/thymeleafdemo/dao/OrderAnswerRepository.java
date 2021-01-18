package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.OrderAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderAnswerRepository extends JpaRepository<OrderAnswer, Integer> {
    @Transactional
    @Modifying
    @Query("delete from OrderAnswer oa where oa.id=:id")
    public void deleteOrderAnswerById(Integer id);

    public List<OrderAnswer> findAllByWorkshopUsername(String username);
    public OrderAnswer findOrderAnswerById(Integer id);
}
