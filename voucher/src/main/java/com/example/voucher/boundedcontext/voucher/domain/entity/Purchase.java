package com.example.voucher.boundedcontext.voucher.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Column;

public class Purchase {

    @SerializedName("phone_number")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @SerializedName("transaction")
    @JsonProperty("transaction")
    private String transaction;

    @SerializedName("amount")
    @JsonProperty("amount")
    private double amount;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTransaction() {
        return transaction;
    }

    public double getAmount() {
        return amount;
    }
}
