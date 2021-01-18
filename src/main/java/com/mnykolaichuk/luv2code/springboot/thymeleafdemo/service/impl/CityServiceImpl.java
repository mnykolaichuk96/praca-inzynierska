package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.CityRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.City;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public List<City> findAll() {
        return cityRepository.findAll();
    }


    @Override
    public City findByCityName(String cityName) {
        return cityRepository.findCityByCityName(cityName);
    }

    @Override
    public List<String> loadCites() {
        List<String> cities = new ArrayList<>();
        for(City city : cityRepository.findAll()){
            cities.add(city.getCityName());
        }
        return cities;
    }
}
