package com.example.voucher.boundedcontext.voucher.exception;

public class TimeoutException extends Exception {

    @Override
    public String getMessage() {
        return "connection timeout";
    }
}
