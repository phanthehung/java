package com.example.purchase.boundedcontext.purchase.application.response;

public class PurchaseVoucherResp {

    private String url;

    private String transaction;

    public PurchaseVoucherResp() {
    }

    public PurchaseVoucherResp(String url, String transaction) {
        this.url = url;
        this.transaction = transaction;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getUrl() {
        return url;
    }
}
