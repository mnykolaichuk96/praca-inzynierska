package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmployeeDetailRepository extends JpaRepository<EmployeeDetail, Integer> {
    public EmployeeDetail findEmployeeDetailByEmail(String email);
    public List<EmployeeDetail> findAllByEmail(String email);
    public EmployeeDetail findEmployeeDetailByEmployeeUsername(String username);
    public EmployeeDetail findEmployeeDetailById(Integer id);


    @Transactional
    @Modifying
    @Query("delete from EmployeeDetail ed where ed.id=:id")
    public void deleteEmployeeDetailById(Integer id);
}
