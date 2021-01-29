package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>, EmployeeRepositoryCustom{
    @Transactional
    @Modifying
    @Query("delete from Employee e where e.id=:id")
    void deleteEmployeeById(Integer id);

    Employee findEmployeeByUsername(String username);
    Employee findEmployeeById(Integer id);
    List<Employee> findAll();
}
