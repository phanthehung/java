package com.example.purchase.boundedcontext.payment;

import com.example.purchase.boundedcontext.purchase.application.PaymentServiceInterface;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

public class PaymentService implements PaymentServiceInterface {
    @Override
    public String getCreditPaymentProviderUrl(CreditPaymentRequest request) throws UnsupportedEncodingException {
        return "{\"transaction\" : \"" + request.getTransaction() +"\",\"phone_number\": \"1233\",\"success\": true,\"amount\" : " + request.getAmount() +"}";
    }

    @Override
    public String encryptPaymentPayload(CreditPaymentRequest request) {
        Gson gson = new Gson();
        return gson.toJson(request);
    }

    @Override
    public String decryptPaymentPayload(String payload, String provider) {
        return payload;
    }
}
