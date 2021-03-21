package com.example.voucher.boundedcontext.voucher.exception;

public class RetryLimitException extends Exception {
    @Override
    public String getMessage() {
        return "reach max number of retry";
    }
}
