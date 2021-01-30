package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.SecureToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SecureTokenRepository extends JpaRepository<SecureToken, Integer> {

    @Transactional
    @Modifying
    @Query("delete from SecureToken st where st.id=:id")
    void deleteSecureTokenById(Integer id);

    SecureToken findByToken(final String token);
    SecureToken findByEmployeeDetail(EmployeeDetail employeeDetail);
}
