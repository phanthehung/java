package com.example.purchase.boundedcontext.purchase.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class GenerateVoucherResp {

    public static final int RESPONSE_PROCESSING = 100;
    public static final int RESPONSE_SUCCESS = 101;
    public static final int RESPONSE_FAILED = 102;

    @JsonProperty("transaction")
    @SerializedName("transaction")
    private String transaction;

    @JsonProperty("voucher_code")
    @SerializedName("voucher_code")
    private String voucherCode;

    @JsonProperty("code")
    @SerializedName("code")
    private int code;

    public GenerateVoucherResp() {
    }

    public GenerateVoucherResp(String transaction, String voucherCode, int code) {
        this.transaction = transaction;
        this.voucherCode = voucherCode;
        this.code = code;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public int getCode() {
        return code;
    }
}
