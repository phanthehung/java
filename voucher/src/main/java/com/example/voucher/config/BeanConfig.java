package com.example.voucher.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.example.voucher.boundedcontext.shared.sms.MockSmsService;
import com.example.voucher.boundedcontext.shared.sms.SmsInterface;
import com.example.voucher.boundedcontext.voucher.application.VoucherServiceInterface;
import com.example.voucher.boundedcontext.shared.messagequeue.MessageQueueInterface;
import com.example.voucher.boundedcontext.voucher.domain.VoucherProviderInterface;
import com.example.voucher.boundedcontext.voucher.domain.VoucherRepositoryInterface;
import com.example.voucher.boundedcontext.voucher.application.VoucherService;
import com.example.voucher.boundedcontext.voucher.infrastructure.MockVoucherProvider;
import com.example.voucher.boundedcontext.shared.messagequeue.SqsMessageQueue;
import com.example.voucher.boundedcontext.voucher.infrastructure.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPool;

import javax.persistence.EntityManager;

@Configuration
public class BeanConfig {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AmazonSQSAsync awsSqsAsync;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public VoucherProviderInterface mockVoucherProvider() {
        return new MockVoucherProvider(messageQueue());
    }

    @Bean
    public VoucherRepositoryInterface voucherRepository() {
        return new VoucherRepository(entityManager);
    }

    @Bean
    public VoucherServiceInterface voucherService() {
        return new VoucherService(voucherRepository(), mockVoucherProvider(), applicationEventPublisher);
    }

    @Bean
    public MessageQueueInterface messageQueue() {
        return new SqsMessageQueue(awsSqsAsync);
    }

    @Bean
    public SmsInterface smsService() {
        return new MockSmsService(messageQueue());
    }
}




