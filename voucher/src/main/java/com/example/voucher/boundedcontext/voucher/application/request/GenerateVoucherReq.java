package com.example.voucher.boundedcontext.voucher.application.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class GenerateVoucherReq {

    @JsonProperty("transaction")
    @SerializedName("transaction")
    private String transaction;

    @JsonProperty("phone_number")
    @SerializedName("phone_number")
    private String phoneNumber;

    @JsonProperty("amount")
    @SerializedName("amount")
    private double amount;

    public GenerateVoucherReq() {
    }

    public GenerateVoucherReq(String transaction, String phoneNumber, double amount) {
        this.transaction = transaction;
        this.phoneNumber = phoneNumber;
        this.amount = amount;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getAmount() {
        return amount;
    }
}
