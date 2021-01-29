package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.EmailAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.UserAlreadyExistException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Employee;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeData;

import java.util.List;

/**
 * Klient przedstawiony w bazie danych za pomocą dwóch tabeli Employee oraz EmployeeDetail.
 * Employee przechowuje username oraz hasło
 * EmployeeDetail przechowuje dane klienta oraz flagę weryfikacji
 * Klient ma możliwość zmiany danych osobistych
 * Jeżeli nie zarejestrowany klient tworze zlecenie, na podstawie przez niego wprowadzonych danych jest tworzony oraz zapisany do bazy danych
 * objekt EmployeeDetail bez przepisanego Entity objektu Employee
 * Jeżeli niezarejestrowany klient tworzył wcześniej zlecenia jego dane już są w bazie danych. Do jego danych przypisane są tworzone przez niego zlecenia.
 * Przy rejestracji takiego klienta lub tworzeniu jeszcze jednego zlecenia jako nie zarejestrowany klient jego nowe dane zapisanę do bazy danych a po weryfikacji
 * adresy email stare zlecenia zostaną przypisane do nowych danych a stare dane zostaną usunięte z bazy danych.
 */

public interface EmployeeService {

    /**
     * Rejestracja klienta na podstawie objektu zawierającego dane, zwróconego z Front End.
     * Zruca wyjątki {@code UsernameAlreadyExistException jeżeli klient albo warsztat z podanum username już istnieje w bazie danych}
     *              {@code EmailAlreadyExistException jeżeli klient z podanym username już istnieje w bazie danych}
     *
     * @param employeeData
     * @throws UserAlreadyExistException
     * @throws EmailAlreadyExistException
     */
    void register(EmployeeData employeeData)
            throws UserAlreadyExistException, EmailAlreadyExistException;

    /**
     * Zmiana danych klienta na podstawie objektu zawierającego nowe dane po akceptacji zmian, zwróconego z Front End, username oraz email adresy przed zmianą.
     * Po zmianie username następuje automatyczne wylogowanie.
     * Po zmianie email adresy następuje automatyczne wylogowanie i klient będzie mógł się zalogować dopiero po weryfikacji email adresy.
     * Zruca wyjątki {@code UsernameAlreadyExistException jeżeli, przy próbie akceptacji zmian, klient albo warsztat z podanum username już istnieje w bazie danych}
     *               {@code EmailAlreadyExistException jeżeli, przy próbie akceptacji zmian, klient z podanym username już istnieje w bazie danych}
     *
     * @param employeeData objekt zawierający nowe dane klienta
     * @param oldUsername username klienta przed rozpoczęciem aktualizacji danych
     * @param oldEmail email klienta przed rozpoczęciem aktualizacji danych
     * @throws UserAlreadyExistException
     * @throws EmailAlreadyExistException
     */
    void update(EmployeeData employeeData, String oldUsername, String oldEmail)
            throws UserAlreadyExistException, EmailAlreadyExistException;

    /**
     * Zwraca objekt służący do wyslania danych klienta z bazy danych na Front End.
     *
     * @param username Employee username
     * @return objekt zawierający dane klienta które są połączeniem objektów EmployeeDetail oraz Employee
     */
    EmployeeData getEmployeeDataByUsername(String username);

    List<EmployeeData> getAllEmployeeDataList();

    /**
     * Usunięcie konta klienta.
     * Jeżeli samochód klienta jest przypisany do innych klientów nie będzie usunięty z bazy danych.
     * Jeżeli klient ma zlecenia w stanie CREATED lub WORKSHOP_ANSWER będą oni usunięte z bazy danych.
     * Jeżeli klient ma zlecenia w stanie IMPLEMENTATION nie będą oni usunięte z bazy danych dopóki będzie relacja z warsztatem. Od razu po usunięciu konta
     *  warsztat do którego przypisane zlecenie o stanie IMPLEMENTATION dostanie pismo email z informacją że klient usunął konto oraz dane kontaktowe klienta
     * Jeżeli klient ma zlecenie w stanie UNREGISTERED ono będzie usunięte z bazy danych jeżeli nie ma przypisanych warsztatów.
     *  W przeciwnym przypadku będzie usunięte relację Klient-Zlecenie.
     * Jeżeli klient ma zlecenie w stanie COMPLETED ono będzie usunięte z bazy danych jeżeli nie ma przypisanych warsztatów.
     *  W przeciwnym przypadku będzie usunięte relację Klient-Zlecenie.
     *
     * @param username Employee username
     */
    void deleteByUsername(String username);

    /**
     * Usunięcie samochodu z bazy danych, jeżeli do niego przypisany jeden klient.
     * Usunięcie relacji Klient-Samochód kiedy di samochodu przypisano więcej klientów.
     *
     * @param carId id usuwanego samochodu
     * @param username Employee username
     */
    void deleteEmployeeCarByCarIdAndUsername(Integer carId, String username);

    /**
     *
     * @param username
     * @return Entity objekt
     */
    Employee findByUsername(String username);

    /**
     *
     * @param id
     * @return Entity objekt
     */
    Employee findById(Integer id);

    boolean comparePassword(String password, String encodePassword);

    boolean isCarInEmployee(Employee employee);
    boolean isEmployeeInEmployeeDetail(EmployeeDetail employeeDetail);









}
