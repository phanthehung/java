package com.example.purchase.boundedcontext.payment;

public class CreditPaymentRequest {

    private String creditNumber;
    private String expireDate;
    private String secretNumber;
    private String transaction;
    private String paymentProvider;
    private double amount;

    public CreditPaymentRequest(String creditNumber, String expireDate, String secretNumber, String transaction, String paymentProvider, double amount) {
        this.creditNumber = creditNumber;
        this.expireDate = expireDate;
        this.secretNumber = secretNumber;
        this.transaction = transaction;
        this.paymentProvider = paymentProvider;
        this.amount = amount;
    }

    public String getCreditNumber() {
        return creditNumber;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public String getSecretNumber() {
        return secretNumber;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }
}
