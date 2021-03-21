package com.example.purchase.boundedcontext.purchase.application.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class PurchaseVoucherReq {
    @JsonProperty("phone_number")
    @SerializedName("phone_number")
    @NotBlank(message = "phone number is required")
    private String phoneNumber;

    @JsonProperty("credit_number")
    @SerializedName("credit_number")
    @Size(min=16, max=16, message = "credit card number is required")
    private String creditNumber;

    @JsonProperty("expire_date")
    @SerializedName("expire_date")
    @NotBlank(message = "expire date is required")
    private String expireDate;

    @JsonProperty("secret_number")
    @SerializedName("secret_number")
    @NotBlank(message = "secret number is required")
    private String secretNumber;

    @JsonProperty("payment_provider")
    @SerializedName("payment_provider")
    @NotBlank(message = "payment vendor is required")
    private String paymentProvider;

    @JsonProperty("amount")
    @SerializedName("amount")
    @NotEmpty(message = "amount is required")
    @Min(1000)
    private double amount;

    public PurchaseVoucherReq() {
    }

    public PurchaseVoucherReq(String phoneNumber, String creditNumber, String expireDate, String secretNumber, String paymentProvider, double amount) {
        this.phoneNumber = phoneNumber;
        this.creditNumber = creditNumber;
        this.expireDate = expireDate;
        this.secretNumber = secretNumber;
        this.paymentProvider = paymentProvider;
        this.amount = amount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public double getAmount() {
        return amount;
    }
}
