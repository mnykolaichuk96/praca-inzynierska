package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeCar;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * W celu umożliwienia weryfikacji relacji ManyToMany Klient-Samochód jest utworzony serwis.
 */

@Service
public interface EmployeeCarService {

    /**
     * Zwraca listę wszystkich relacji Klient-Samochód dla samochodu.
     *
     * @param carId
     * @return List Entity objektów
     */
    List<EmployeeCar> findAllByCarId(Integer carId);

    /**
     * Zwraca listę wszystkich relacji
     *
     * @param employeeId
     * @return
     */
    List<EmployeeCar> findAllByEmployeeId(Integer employeeId);

    /**
     * Żeby zwrócić unikatowy Entity objekt potrzebne są id klienta oraz samochodu.
     *
     * @param employeeId
     * @param carId
     * @return Entity objekt
     */
    EmployeeCar findByEmployeeIdAndCarId(Integer employeeId, Integer carId);

    /**
     * Usuwa z bazy danych.
     *
     * @param employeeCar Entity objekt
     */
    void delete(EmployeeCar employeeCar);

    /**
     * Zapisuje Entity objekt do bazy danych
     *
     * @param employeeCar
     */
    void save(EmployeeCar employeeCar);
}
