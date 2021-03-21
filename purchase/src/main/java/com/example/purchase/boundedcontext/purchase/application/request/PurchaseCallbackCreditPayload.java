package com.example.purchase.boundedcontext.purchase.application.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class PurchaseCallbackCreditPayload {

    @JsonProperty("transaction")
    @SerializedName("transaction")
    private String transaction;

    @JsonProperty("amount")
    @SerializedName("amount")
    private double amount;

    @JsonProperty("success")
    @SerializedName("success")
    private boolean success;

    public PurchaseCallbackCreditPayload() {
    }

    public PurchaseCallbackCreditPayload(String transaction, double amount, boolean success) {
        this.transaction = transaction;
        this.amount = amount;
        this.success = success;
    }

    public String getTransaction() {
        return transaction;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isSuccess() {
        return success;
    }
}
