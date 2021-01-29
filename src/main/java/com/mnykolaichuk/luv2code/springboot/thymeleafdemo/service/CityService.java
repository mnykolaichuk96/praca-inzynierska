package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.CantDeleteCityException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.City;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.CityData;

import java.util.List;

/**
 * Klienty mają dostep do wyboru miasta przy tworzeniu zlecenia.
 * Warsztaty mają dostęp do wyboru miasta przy rejestracji.
 * Admin moze dodawać lub usuwać, jeżeli u nich nie ma zarejestrowanych warsztatów, miasta.
 */

public interface CityService {
    /**
     *
     * @param cityName
     * @return Entity objekt
     */
    City findByCityName(String cityName);

    /**
     * Zwraca listę wszystkich dostępnych miast.
     *
     * @return List Entity objektów
     */
    List<String> loadCites();

    List<CityData> getAllCityDataList();

    void deleteCityById(Integer id) throws CantDeleteCityException;

    void addCity(City city);
}
