package com.example.voucher.boundedcontext.voucher.application.request;

import com.google.gson.annotations.SerializedName;

public class VoucherGenerationRetryReq {

    @SerializedName(value = "transaction")
    private String transaction;

    @SerializedName(value = "amount")
    private double amount;

    public VoucherGenerationRetryReq() {
    }

    public VoucherGenerationRetryReq(String transaction, double amount) {
        this.transaction = transaction;
        this.amount = amount;
    }

    public String getTransaction() {
        return transaction;
    }

    public double getAmount() {
        return amount;
    }
}
