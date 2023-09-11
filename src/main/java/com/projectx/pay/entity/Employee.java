package com.projectx.pay.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Table(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "firstname", nullable = false, length = 25)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 25)
    private String lastname;


    @Column(name = "gender", nullable = false, length = 30)
    private String gender;


    @Column(name = "msisdn", nullable = false, length = 30)
    private String msisdn;


    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "department", nullable = false)
    private String department;


    @Column(name = "age", nullable = false)
    private String age;


    @Column(name = "employment_date", nullable = false)
    private String employment_date;


    @Column(name = "national_id", nullable = false)
    private String national_id;


    @Column(name = "kra_pin", nullable = false)
    private String kra_pin;


    @Column(name = "nssf", nullable = false)
    private String nssf;


    @Column(name = "nhif", nullable = false)
    private String nhif;


    @Column(name = "username", nullable = false)
    private String username;


    @Column(name = "password", nullable = false)
    private String password;


    @Column(name = "created_on", nullable = false)
    private Instant createdOn;

    @Column(name = "updated_on", nullable = false)
    private Instant updatedOn;


}