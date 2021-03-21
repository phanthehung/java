package com.example.purchase.boundedcontext.shared.sms;

public class MockSmsService implements SmsInterface {
    @Override
    public void sendMessage(String phoneNumber, String content) {
        String message = "phone number: " + phoneNumber + " | " + content;
        System.out.println("-----------------------------------------------------------------");
        System.out.println();
        System.out.println();
        System.out.println(message);
        System.out.println();
        System.out.println();
        System.out.println("-----------------------------------------------------------------");
    }
}
