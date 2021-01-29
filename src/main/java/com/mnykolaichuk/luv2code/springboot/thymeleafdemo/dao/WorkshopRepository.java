package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.City;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WorkshopRepository  extends JpaRepository<Workshop, Integer>, WorkshopRepositoryCustom {
    @Transactional
    @Modifying
    @Query("delete from Workshop w where w.id=:id")
    void deleteWorkshopById(Integer id);

    Workshop findWorkshopByUsername(String username);
    Workshop findWorkshopByEmail(String email);
    Workshop findWorkshopById(Integer id);
    List<Workshop> findAllByCity(City city);
    List<Workshop> findAll();

}
