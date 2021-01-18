package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.City;

import java.util.List;

public interface CityService {
    public List<City> findAll();
    public City findByCityName(String cityName);
    public List<String> loadCites();
}
