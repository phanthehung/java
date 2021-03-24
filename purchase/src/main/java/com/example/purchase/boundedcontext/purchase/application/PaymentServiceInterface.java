package com.example.purchase.boundedcontext.purchase.application;

import com.example.purchase.boundedcontext.payment.CreditPaymentRequest;

import java.io.UnsupportedEncodingException;

public interface PaymentServiceInterface {
    public String getCreditPaymentProviderUrl(CreditPaymentRequest request) throws UnsupportedEncodingException;

    public String encryptPaymentPayload(CreditPaymentRequest request);

    public String decryptPaymentPayload(String payload, String provider);
}
