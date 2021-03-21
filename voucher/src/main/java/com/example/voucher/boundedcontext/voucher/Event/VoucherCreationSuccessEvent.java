package com.example.voucher.boundedcontext.voucher.Event;

import org.springframework.context.ApplicationEvent;

public class VoucherCreationSuccessEvent extends ApplicationEvent {

    private String transaction;
    private String phoneNumber;
    private String voucherCode;
    private double amount;

    public VoucherCreationSuccessEvent(Object source, String transaction, String phoneNumber, String voucherCode, double amount) {
        super(source);
        this.transaction = transaction;
        this.phoneNumber = phoneNumber;
        this.voucherCode = voucherCode;
        this.amount = amount;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public double getAmount() {
        return amount;
    }
}
