package com.example.sms;

import com.google.gson.Gson;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SmsQueueListener {

    @SqsListener(value = "SMS_QUEUE", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void smsQueueListener(String message) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(message);
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("-----------------------------------------------------------------");
    }
}
