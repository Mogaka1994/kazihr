package com.projectx.pay.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"firstname","lastname","username","password","email","status","admin","msisdn","role","amount","reference"})
public class UserModel {
    @JsonProperty("firstname")
    private String firstname;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("email")
    private String email;
    @JsonProperty("status")
    private String status;
    @JsonProperty("admin")
    private Integer admin;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("reference")
    private String reference;
    @JsonProperty("role")
    private Set<String> role;

}
