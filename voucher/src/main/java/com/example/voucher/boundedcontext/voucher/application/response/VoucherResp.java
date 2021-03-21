package com.example.voucher.boundedcontext.voucher.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class VoucherResp {

    @SerializedName("voucher_code")
    @JsonProperty("voucher_code")
    private String voucherCode;

    @SerializedName("transaction")
    @JsonProperty("transaction")
    private String transaction;

    @SerializedName("status")
    @JsonProperty("status")
    private String status;

    @SerializedName("amount")
    @JsonProperty("amount")
    private double amount;

    public VoucherResp() {
    }

    public VoucherResp(String voucherCode, String transaction, String status, double amount) {
        this.voucherCode = voucherCode;
        this.transaction = transaction;
        this.status = status;
        this.amount = amount;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getStatus() {
        return status;
    }

    public double getAmount() {
        return amount;
    }
}