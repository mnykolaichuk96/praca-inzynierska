package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.City;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkshopRepository  extends JpaRepository<Workshop, Integer>, WorkshopRepositoryCustom {

    public Workshop findWorkshopByUsername(String username);
    public Workshop findWorkshopByEmail(String email);
    public List<Workshop> findAllByCity(City city);

}
