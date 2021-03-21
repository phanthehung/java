package com.example.voucher.boundedcontext.shared.messagequeue;

public interface MessageQueueInterface  {
    void sendMessage(String queue, String payload);
}
