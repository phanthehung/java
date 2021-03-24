package com.example.voucher.boundedcontext.shared.sms;

import com.example.voucher.boundedcontext.shared.messagequeue.MessageQueueInterface;
import org.springframework.stereotype.Component;

@Component
public class MockSmsService implements SmsInterface {


    private MessageQueueInterface messageQueue;

    public MockSmsService(MessageQueueInterface messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void sendMessage(String phoneNumber, String content) {
        String message = "phone number: " + phoneNumber + " | " + content;
        messageQueue.sendMessage("SMS_QUEUE", message);

    }
}
