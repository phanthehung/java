package com.example.purchase.boundedcontext.purchase.domain;

import com.example.purchase.boundedcontext.payment.CreditPaymentRequest;

public interface PaymentServiceInterface {
    public String getCreditPaymentProviderUrl(CreditPaymentRequest request);

    public String encryptPaymentPayload(CreditPaymentRequest request);

    public String decryptPaymentPayload(String payload, String provider);
}
