package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CityRepository  extends JpaRepository<City, Integer>, CityRepositoryCustom {

    @Transactional
    @Modifying
    @Query("delete from City c where c.id=:id")
    void deleteCityById(Integer id);

    City findCityByCityName(String cityName);
    City findCityById(Integer id);
}
