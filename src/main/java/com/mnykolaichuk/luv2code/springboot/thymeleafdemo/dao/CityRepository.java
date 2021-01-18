package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository  extends JpaRepository<City, Integer>, CityRepositoryCustom {
    public City findCityByCityName(String cityName);
}
