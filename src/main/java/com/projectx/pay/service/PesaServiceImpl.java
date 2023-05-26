package com.projectx.pay.service;

import com.projectx.pay.entity.AdvancePayments;
import com.projectx.pay.globalFunctions.BeareTokenService;
import com.projectx.pay.globalFunctions.GlobalFunctions;
import jakarta.annotation.PostConstruct;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Component
public class PesaServiceImpl implements PesaService {

    @Autowired
    Environment env;

    GlobalFunctions globalFunctions = new GlobalFunctions();

    @Autowired
    CrudService crudeService;


    @Autowired
    UserService userService;

    String stk_push_shortcode, passkey, stk_end_point, callbackurl;
    final Logger LOGGER = LoggerFactory.getLogger(PesaServiceImpl.class.getSimpleName());
    final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");


    @PostConstruct
    public void init() {
        stk_push_shortcode = env.getRequiredProperty("mpesa.shortcode");
        passkey = env.getRequiredProperty("mpesa.passkey");
        stk_end_point = env.getRequiredProperty("mpesa.stkpush");
        callbackurl = env.getRequiredProperty("mpesa.callback");
    }


    @Override
    public String processAdvanceRepayment(String msisdn, String amount, String ref) {
        Date now = new Date(System.currentTimeMillis());
        try {
            String _timestamp = SDF.format(now);
            String requestBody = "{\n"
                    + "      \"BusinessShortCode\": \"" + stk_push_shortcode + "\",\n"
                    + "      \"Password\": \"" + globalFunctions.getRequestPassword(stk_push_shortcode, passkey, _timestamp) + "\",\n"
                    + "      \"Timestamp\": \"" + _timestamp + "\",\n"
                    + "      \"TransactionType\": \"CustomerPayBillOnline\",\n"
                    + "      \"Amount\": \"" + amount + "\",\n"
                    + "      \"PartyA\": \"" + msisdn + "\",\n"
                    + "      \"PartyB\": \"" + stk_push_shortcode + "\",\n"
                    + "      \"PhoneNumber\": \"" + msisdn + "\",\n"
                    + "      \"CallBackURL\": \"" + callbackurl + "\",\n"
                    + "      \"AccountReference\": \"" + ref + "\",\n"
                    + "      \"TransactionDesc\": \"STK Push\"\n"
                    + "    }";
            LOGGER.info("REQBODY={}",requestBody);
            String response = globalFunctions.postPayload(stk_end_point, requestBody, "Bearer " + BeareTokenService.token);
            LOGGER.info("response={}", msisdn, response);
            JSONObject jsonObjectResponse = new JSONObject(response);
            String MerchantRequestID = jsonObjectResponse.get("MerchantRequestID").toString();
            String CheckoutRequestID = jsonObjectResponse.get("CheckoutRequestID").toString();
            String ResponseCode = jsonObjectResponse.get("ResponseCode").toString();
            if (ResponseCode.contentEquals("0")) {
                AdvancePayments trx = new AdvancePayments();
                trx.setTrx_type("STK_PUSH");
                trx.setSender_party(msisdn);
                trx.setReceiver_party(stk_push_shortcode);
                trx.setCompleted_date(now);
                trx.setBillrefnumber(ref);
                trx.setProcessing_status("Initiated");
                trx.setAmount(amount);
                trx.setMsisdn(msisdn);
                trx.setRequest_id(MerchantRequestID);
                trx.setCheckout_id(CheckoutRequestID);
                crudeService.save(trx);
                return trx.toString();
            }
        } catch (Exception e) {
            LOGGER.error("response={}", e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Long processCallBack(String req) throws JSONException {

        String amount,mpesa_receipt;

        JSONObject jsonObject = new JSONObject(req);
        JSONObject callBackData = jsonObject.getJSONObject("Body").getJSONObject("stkCallback");

        String merchantReqId= callBackData.getString("MerchantRequestID");

        String metaData = callBackData.getString("CallbackMetadata");

        JSONArray object = new JSONObject(metaData).getJSONArray("Item");

        amount = object.getJSONObject(0).getString("Value");

        mpesa_receipt = object.getJSONObject(1).getString("Value");

        String  sql = "UPDATE advance_payments SET MPESA_RECEIPT='"+mpesa_receipt+"',AMOUNT='"+amount+"',PROCESSING_STATUS='COMPLETED' WHERE MERCHANT_REQUEST_ID ='"+merchantReqId+"'";

       return Long.valueOf(crudeService.executeNativeQuery(sql, Collections.EMPTY_MAP));
    }
}

