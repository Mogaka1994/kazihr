package com.projectx.pay.service;

import org.json.JSONException;

public interface PesaService {

    String processAdvanceRepayment(String msisdn, String amount, String ref);

    Long processCallBack(String request) throws JSONException;
}
