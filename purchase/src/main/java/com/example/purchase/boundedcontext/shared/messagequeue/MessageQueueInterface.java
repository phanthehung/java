package com.example.purchase.boundedcontext.shared.messagequeue;

public interface MessageQueueInterface {

    void sendMessage(String queue, String payload);
}
