package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.enums.AuthorityEnum;

import javax.persistence.*;

@Entity
@Table(name = "authority")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private AuthorityEnum authority;

    public Authority() { }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AuthorityEnum getAuthority() {
        return authority;
    }

    public void setAuthority(AuthorityEnum authority) {
        this.authority = authority;
    }
}
