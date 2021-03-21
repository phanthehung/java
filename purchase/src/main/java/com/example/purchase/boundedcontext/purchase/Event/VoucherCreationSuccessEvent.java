package com.example.purchase.boundedcontext.purchase.Event;

import org.springframework.context.ApplicationEvent;

public class VoucherCreationSuccessEvent extends ApplicationEvent {

    private String transaction;

    public VoucherCreationSuccessEvent(Object source, String transaction) {
        super(source);
        this.transaction = transaction;
    }

    public String getTransaction() {
        return transaction;
    }
}
