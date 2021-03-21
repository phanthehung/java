package com.example.purchase.boundedcontext.payment;

import com.example.purchase.boundedcontext.purchase.domain.PaymentServiceInterface;
import com.google.gson.Gson;

public class PaymentService implements PaymentServiceInterface {
    @Override
    public String getCreditPaymentProviderUrl(CreditPaymentRequest request) {

        return "payment.com?callback=/callback/voucher/credit?payload=" + this.encryptPaymentPayload(request);
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
