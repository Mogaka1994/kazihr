package com.projectx.pay.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectx.pay.model.UserModel;
import com.projectx.pay.service.PesaService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class FinanceController {

    @Autowired
    PesaService pesaService;

    Map<String, Object> responseMap = new HashMap<>();


    @PostMapping(value="/repay")
    public ResponseEntity<String> SaveMoney(@RequestBody UserModel payload) throws  JsonProcessingException {
        try {
            String resp = pesaService.processAdvanceRepayment(payload.getMsisdn(),payload.getAmount(),payload.getReference());
            if (resp != null) {
                responseMap.put("responseCode", "00");
            } else {
                responseMap.put("responseCode", "01");
            }
            responseMap.put("responseMessage","Request Received for Processing" );
            return new ResponseEntity<>(new ObjectMapper().writeValueAsString(responseMap), HttpStatus.OK);
        } catch (JsonProcessingException e) {
            responseMap.put("responseMessage", e.getMessage());
            return new ResponseEntity<>(new ObjectMapper().writeValueAsString(responseMap), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/stkCallBack")
    public ResponseEntity<String> processCallBack(@RequestBody String payload) throws JSONException, JsonProcessingException {
       int resp = Math.toIntExact(pesaService.processCallBack(payload));
        if (resp == 1) {
            responseMap.put("responseCode", "00");
            responseMap.put("responseMessage","Request Received for Processing" );
        } else {
            responseMap.put("responseCode", "01");
            responseMap.put("responseMessage","Error processing request" );
        }
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(responseMap), HttpStatus.OK);

    }
}
