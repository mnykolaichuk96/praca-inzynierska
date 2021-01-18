package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "token")
    private String token;

    @OneToOne
    @MapsId
    @JoinColumn(name = "employee_detail_id")
    private EmployeeDetail employeeDetail;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "employee_car"
            , joinColumns = @JoinColumn(name = "employee_id")
            , inverseJoinColumns = @JoinColumn(name = "car_id"))
    private Set<Car> cars;


    @ManyToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "employee_authority"
            , joinColumns = @JoinColumn(name = "employee_id")
            , inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private Set<Authority> authorities;

    public Employee() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public EmployeeDetail getEmployeeDetail() {
        return employeeDetail;
    }

    public void setEmployeeDetail(EmployeeDetail employeeDetail) {
        this.employeeDetail = employeeDetail;
    }

    public Set<Car> getCars() {
        return cars;
    }

    public void setCars(Set<Car> cars) {
        this.cars = cars;
    }

    public Collection<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

}
