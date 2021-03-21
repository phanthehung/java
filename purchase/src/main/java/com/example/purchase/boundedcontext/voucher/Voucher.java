package com.example.purchase.boundedcontext.voucher;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class Voucher {

    @JsonProperty("transaction")
    @SerializedName("transaction")
    private String transaction;

    @JsonProperty("voucher_code")
    @SerializedName("voucher_code")
    private String voucherCode;

    @JsonProperty("amount")
    @SerializedName("amount")
    private double amount;

    @JsonProperty("status")
    @SerializedName("status")
    private String status;

    public Voucher() {
    }

    public Voucher(String transaction, String voucherCode, double amount, String status) {
        this.transaction = transaction;
        this.voucherCode = voucherCode;
        this.amount = amount;
        this.status = status;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
