package com.example.purchase.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.example.purchase.Util.RandomStringGenerator;
import com.example.purchase.Util.UniqueStringGenerator;
import com.example.purchase.boundedcontext.payment.PaymentService;
import com.example.purchase.boundedcontext.purchase.application.PaymentServiceInterface;
import com.example.purchase.boundedcontext.purchase.application.PurchaseService;
import com.example.purchase.boundedcontext.purchase.application.PurchaseServiceInterface;
import com.example.purchase.boundedcontext.purchase.application.VoucherServiceInterface;
import com.example.purchase.boundedcontext.purchase.domain.*;
import com.example.purchase.boundedcontext.purchase.infrastructure.PurchaseRepository;
import com.example.purchase.boundedcontext.shared.messagequeue.MessageQueueInterface;
import com.example.purchase.boundedcontext.shared.messagequeue.SqsMessageQueue;
import com.example.purchase.boundedcontext.shared.sms.MockSmsService;
import com.example.purchase.boundedcontext.shared.sms.SmsInterface;
import com.example.purchase.boundedcontext.voucher.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import java.time.Duration;

@Configuration
public class BeanConfig {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AmazonSQSAsync awsSqsAsync;

    @Bean
    public RestTemplate restTemplate() {
        return (new RestTemplateBuilder()).setReadTimeout(Duration.ofSeconds(10)).build();
    }

    @Bean
    public PurchaseRepositoryInterface purchaseRepository() {
        return new PurchaseRepository(entityManager);
    }

    @Bean
    public VoucherServiceInterface voucherService() {
        return new VoucherService(restTemplate(), messageQueue());
    }

    @Bean
    public MessageQueueInterface messageQueue() {
        return new SqsMessageQueue(awsSqsAsync);
    }

    @Bean
    public PurchaseServiceInterface purchaseService() {
        return new PurchaseService(purchaseRepository(), voucherService(), uniqueStringGenerator());
    }

    @Bean
    public UniqueStringGenerator uniqueStringGenerator() {
        return new RandomStringGenerator();
    }

    @Bean
    public PaymentServiceInterface paymentService() {
        return new PaymentService();
    }

    @Bean
    public SmsInterface mockSmsService() {
        return new MockSmsService(messageQueue());
    }
}
