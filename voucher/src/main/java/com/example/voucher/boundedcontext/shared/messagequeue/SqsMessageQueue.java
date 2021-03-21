package com.example.voucher.boundedcontext.shared.messagequeue;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.example.voucher.boundedcontext.shared.messagequeue.MessageQueueInterface;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;

public class SqsMessageQueue implements MessageQueueInterface {

    private final QueueMessagingTemplate queueMessagingTemplate;

    public SqsMessageQueue(AmazonSQSAsync amazonSqs) {
        this.queueMessagingTemplate = new QueueMessagingTemplate(amazonSqs);
    }

    @Override
    public void sendMessage(String queue, String payload) {
        this.queueMessagingTemplate.send(queue, MessageBuilder.withPayload(payload).build());
    }
}
