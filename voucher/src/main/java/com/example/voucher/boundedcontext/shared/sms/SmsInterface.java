package com.example.voucher.boundedcontext.shared.sms;

public interface SmsInterface {

    void sendMessage(String phoneNumber, String content);
}
