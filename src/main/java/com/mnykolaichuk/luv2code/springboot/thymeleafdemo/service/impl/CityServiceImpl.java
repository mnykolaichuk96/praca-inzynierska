package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.impl;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.CityRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.CantDeleteCityException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.City;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.CityData;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.CityService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.WorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private WorkshopService workshopService;

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

    @Override
    public List<CityData> getAllCityDataList() {
        List<CityData> allCityDataList = new ArrayList<>();
        CityData cityData;
        for(City city : cityRepository.findAll()) {
            cityData = new CityData();
            cityData.setCityId(city.getId());
            cityData.setCityName(city.getCityName());
            cityData.setWorkshopCount(workshopService.findAllWithUnActiveByCity(city).size());
            allCityDataList.add(cityData);
        }
        return allCityDataList;
    }

    @Override
    public void deleteCityById(Integer id) throws CantDeleteCityException {
        if(workshopService.findAllWithUnActiveByCity(cityRepository.findCityById(id)).size() != 0) {
            throw new CantDeleteCityException("Nie można usunąć dopóki w mieście są warsztaty");
        }
        else {
            cityRepository.deleteCityById(id);
        }
    }

    @Override
    public void addCity(City city) {
        cityRepository.save(city);
    }
}
