package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.SecureToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecureTokenRepository extends JpaRepository<SecureToken, Integer> {

    SecureToken findByToken(final String token);
    Long removeByToken(String token);
}
