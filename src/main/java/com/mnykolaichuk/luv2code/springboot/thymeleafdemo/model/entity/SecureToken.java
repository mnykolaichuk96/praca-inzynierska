package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "secure_token")
public class SecureToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "verification_car_id")
    private Integer verificationCarId;

    @Column(name = "verification_username")
    private String verificationUsername;

    @CreationTimestamp
    @Column(name = "time_stamp")
    private Timestamp timeStamp;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @Column(name = "not_new_employee_detail")
    private Boolean notNewEmployeeDetail;

   @ManyToOne
   @JoinColumn(name = "employee_detail_id")
   private EmployeeDetail employeeDetail;

    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

   @Transient
   private boolean isExpired;

    public SecureToken() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getVerificationCarId() {
        return verificationCarId;
    }

    public void setVerificationCarId(Integer verificationCarId) {
        this.verificationCarId = verificationCarId;
    }

    public String getVerificationUsername() {
        return verificationUsername;
    }

    public void setVerificationUsername(String verificationUsername) {
        this.verificationUsername = verificationUsername;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }


    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public Boolean isNotNewEmployeeDetail() {
        return notNewEmployeeDetail;
    }

    public void setNotNewEmployeeDetail(Boolean newEmployeeDetail) {
        this.notNewEmployeeDetail = newEmployeeDetail;
    }

    public EmployeeDetail getEmployeeDetail() {
        return employeeDetail;
    }

    public void setEmployeeDetail(EmployeeDetail employeeDetail) {
        this.employeeDetail = employeeDetail;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }
}
