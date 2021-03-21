package com.example.purchase.boundedcontext.shared.sms;

public interface SmsInterface {

    void sendMessage(String phoneNumber, String content);
}
