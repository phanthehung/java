package com.example.voucher.boundedcontext.voucher.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class VoucherGenerationResp {

    @JsonProperty("transaction")
    @SerializedName("transaction")
    private String transaction;

    @JsonProperty("voucher_code")
    @SerializedName("voucher_code")
    private String voucherCode;

    @JsonProperty("code")
    @SerializedName("code")
    private int code;

    public VoucherGenerationResp() {
    }

    public VoucherGenerationResp(String transaction, String voucherCode, int code) {
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
