package com.example.purchase.boundedcontext.shared.sms;

import com.example.purchase.boundedcontext.shared.messagequeue.MessageQueueInterface;

public class MockSmsService implements SmsInterface {

    private MessageQueueInterface queue;

    public MockSmsService(MessageQueueInterface queue) {
        this.queue = queue;
    }

    @Override
    public void sendMessage(String phoneNumber, String content) {
        String message = "phone number: " + phoneNumber + " | " + content;
        queue.sendMessage("SMS_QUEUE", message);
    }
}
