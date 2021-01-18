package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Authority;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.AuthorityEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer>, AuthorityRepositoryCustom {

    public Authority findByAuthority(AuthorityEnum authorityEnum);
}
