package com.example.purchase.boundedcontext.purchase.application.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class PurchaseCallbackCreditReq {

    @JsonProperty("partner_name")
    @SerializedName("partner_name")
    private String partnerName;

    @JsonProperty("payload")
    @SerializedName("payload")
    private String payload;

    public PurchaseCallbackCreditReq() {
    }

    public PurchaseCallbackCreditReq(String partnerName, String payload) {
        this.partnerName = partnerName;
        this.payload = payload;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getPayload() {
        return payload;
    }
}
