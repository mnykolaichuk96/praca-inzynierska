package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.exception.InvalidTokenException;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.EmployeeDetail;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entityData.EmployeeDetailData;
import org.springframework.stereotype.Service;

/**
 * Klient przedstawiony w bazie danych za pomocą dwóch tabelek Employee oraz EmployeeDetail.
 * Employee przechowuje username oraz hasło
 * EmployeeDetail przechowuje dane klienta oraz flagę weryfikacji
 * Jeżeli nie zarejestrowany klient tworze zlecenie, na podstawie przez niego wprowadzonych danych jest tworzony oraz zapisany do bazy danych
 * objekt EmployeeDetail bez przepisanego Entity objektu Employee
 * Jeżeli niezarejestrowany klient tworzył wcześniej zlecenia jego dane już są w bazie danych. Do jego danych przypisane są tworzone przez niego zlecenia.
 * Przy rejestracji takiego klienta lub tworzeniu jeszcze jednego zlecenia jako nie zarejestrowany klient jego nowe dane zapisanę do bazy danych a po weryfikacji
 * adresy email stare zlecenia zostaną przypisane do nowych danych a stare dane zostaną usunięte z bazy danych.
 */

@Service
public interface EmployeeDetailService {

    /**
     * Dopóki klient, który tworzył zlecenie jako niezarejestrowany, nie zweryfikuje swój email adres w bazie danych są zapisane dwa objekty zawierające dane klienta
     * z jednakowym adresem email. W takim przypadku będie zwrócony Entity objekt danych stworzonech przy wcieśniejszym wykorzystaniu adresu email.
     *
     * @param email
     * @return Entity objekt
     */
    EmployeeDetail findByEmail(String email);

    /**
     * Ponieważ EmployeeDetail związany z Entity objektem Employee relacją OneToOne, zwraca Entity objekt EmployeeDetail na podstawie pola
     * username przechowanego w tabeli Employee
     *
     * @param username Employee username
     * @return Entity objekt
     */
    EmployeeDetail findByEmployeeUsername(String username);

    /**
     *
     * @param id
     * @return Entity objekt
     */
    EmployeeDetail findById(Integer id);

    /**
     * Usunięcie EmployeeDetail z bazy danych.
     *
     * @param employeeDetail
     */
    void delete(EmployeeDetail employeeDetail);

    /**
     * Zapisanie EmployeeDetail do bazy danych.
     *
     * @param employeeDetail
     */
    void save(EmployeeDetail employeeDetail);

    /**
     * Zwraca objekt służący do wyslania danych klienta z bazy danych na Front End.
     *
     * @param employeeDetail Entity objekt
     * @return objekt zawierający dane klienta które są połączeniem objektów EmployeeDetail oraz Employee
     */
    EmployeeDetailData getEmployeeDetailData(EmployeeDetail employeeDetail);

    /**
     * Wysylanie pisma w celu weryfikacji adresu e-mail.
     *
     * @param employeeDetail
     */
    void sendEmployeeDetailEmailVerificationEmail(EmployeeDetail employeeDetail);

    /**
     * Wysylanie pisma w celu weryfikacji adresu e-mail dla EmployeeDetail jeżeli dane klienta jyż są w bazie danych.
     *
     * @param employeeDetail
     */
    void sendNotNewEmployeeDetailEmailVerificationEmail(EmployeeDetail employeeDetail);

    /**
     * Metod wywołany przy przechodzeniu po linku weryfikacji adresy email, jeżeli dane klienta wcześniej były w bazie danych, stare dane są usunięte,
     * zlecenia przepisane do nowych danych.
     * Sprawdza czy tokien jest jeszcze aktualny oraz poprawny. Zruca wyjątek {@code InvalidTokenException} leżeli tokien jest nie poprawny.
     *
     * @param token
     * @return true or false
     * @throws InvalidTokenException
     */
    boolean verifyEmployee(String token) throws InvalidTokenException;

    boolean isOrderInEmployeeDetail(EmployeeDetail employeeDetail);
    boolean isEmployeeInEmployeeDetail(EmployeeDetail employeeDetail);

}
