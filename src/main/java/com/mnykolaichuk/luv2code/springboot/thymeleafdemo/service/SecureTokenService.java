package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Car;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.SecureToken;
import org.springframework.stereotype.Service;

@Service
public interface SecureTokenService {

    public SecureToken createSecureToken();
    public SecureToken createSecureTokenForCar(Car car, String fromEmployee);
    public void saveSecureToken(SecureToken token);
    public SecureToken findByToken(String token);
    public void removeToken(SecureToken token);
    public void removeTokenByToken(String token);
}
