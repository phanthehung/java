package com.example.purchase.boundedcontext.purchase.exception;

import com.example.purchase.boundedcontext.purchase.domain.entity.Purchase;

public class InvalidStateTransitionException extends Exception {

    private Purchase.Status current;
    private Purchase.Status next;
    private String transaction;

    public InvalidStateTransitionException(Purchase.Status current, Purchase.Status next, String transaction) {
        this.current = current;
        this.next = next;
        this.transaction = transaction;
    }

    @Override
    public String getMessage() {
        return "Cannot move transaction " + transaction + " from " + current + " to " + next;
    }
}
