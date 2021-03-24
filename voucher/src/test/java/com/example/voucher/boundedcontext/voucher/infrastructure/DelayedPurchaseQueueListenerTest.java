package com.example.voucher.boundedcontext.voucher.infrastructure;

import com.example.voucher.boundedcontext.shared.messagequeue.MessageQueueInterface;
import com.example.voucher.boundedcontext.voucher.application.VoucherServiceInterface;
import com.example.voucher.boundedcontext.voucher.application.request.VoucherGenerationRetryReq;
import com.example.voucher.boundedcontext.voucher.application.response.VoucherGenerationResp;
import com.example.voucher.boundedcontext.voucher.application.VoucherService;
import com.example.voucher.boundedcontext.voucher.domain.entity.Purchase;
import com.example.voucher.boundedcontext.voucher.domain.entity.Voucher;
import com.example.voucher.boundedcontext.voucher.exception.RetryLimitException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherProcessingException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherStatusConflictException;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class DelayedPurchaseQueueListenerTest {

    @InjectMocks
    private DelayedPurchaseQueueListener listener;

    @Mock
    private VoucherServiceInterface voucherService;

    @Mock
    private MessageQueueInterface messageQueue;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateVoucherSuccess() throws VoucherStatusConflictException, VoucherProcessingException {
        Gson gson = new Gson();
        String phoneNumber = "3123213";
        String transaction = "sdadsa";
        double amount = 56432;
        Purchase purchase = new Purchase(phoneNumber, transaction, amount);
        String message = gson.toJson(purchase);
        String voucherCode = "code";

        String queueMessage = gson.toJson(new VoucherGenerationResp(purchase.getTransaction(), voucherCode, VoucherService.RESPONSE_SUCCESS));

        Mockito.when(voucherService.generateVoucher(Mockito.anyString(), Mockito.anyDouble(), Mockito.anyString())).thenReturn(voucherCode);
        Mockito.doNothing().when(messageQueue).sendMessage(DelayedPurchaseQueueListener.RESPONSE_VOUCHER_GENERATION, queueMessage);
        listener.createVoucher(message);

        Mockito.verify(messageQueue, Mockito.only()).sendMessage(DelayedPurchaseQueueListener.RESPONSE_VOUCHER_GENERATION, queueMessage);
    }

    @ParameterizedTest
    @MethodSource("testCreateVoucherExceptionParameter")
    public void testCreateVoucherException(Purchase purchase, String voucherCode, Voucher.Status status, int code) throws VoucherStatusConflictException, VoucherProcessingException {
        Gson gson = new Gson();
        String message = gson.toJson(purchase);

        Voucher voucher = new Voucher(purchase.getPhoneNumber(), purchase.getTransaction(), purchase.getAmount(), 3, voucherCode, LocalDateTime.now(), status);
        String queueMessage = gson.toJson(new VoucherGenerationResp(purchase.getTransaction(), voucherCode, code));

        Mockito.when(voucherService.generateVoucher(Mockito.anyString(), Mockito.anyDouble(), Mockito.anyString())).thenThrow(new VoucherStatusConflictException(voucher));
        Mockito.doNothing().when(messageQueue).sendMessage(DelayedPurchaseQueueListener.RESPONSE_VOUCHER_GENERATION, queueMessage);
        listener.createVoucher(message);

        Mockito.verify(messageQueue, Mockito.only()).sendMessage(DelayedPurchaseQueueListener.RESPONSE_VOUCHER_GENERATION, queueMessage);
    }

    @Test
    public void testRetryCreateVoucherSuccess() throws VoucherProcessingException, RetryLimitException {
        Gson gson = new Gson();
        String transaction = "ghsfh";
        double amount = 5432;
        String voucherCode = "code";
        Acknowledgment ack = Mockito.mock(Acknowledgment.class);
        Mockito.when(ack.acknowledge()).thenReturn(null);
        VoucherGenerationRetryReq req = new VoucherGenerationRetryReq(transaction, amount);
        VoucherGenerationResp resp = new VoucherGenerationResp(transaction, voucherCode, VoucherService.RESPONSE_SUCCESS);

        String message = gson.toJson(req);
        Mockito.when(voucherService.retryCreateVoucher(transaction, amount)).thenReturn(voucherCode);

        listener.retryCreateVoucher(ack, message);
        Mockito.verify(messageQueue, Mockito.only()).sendMessage(DelayedPurchaseQueueListener.RESPONSE_VOUCHER_GENERATION, gson.toJson(resp));
    }

    @Test
    public void testRetryCreateVoucherException() throws RetryLimitException, VoucherProcessingException {
        Gson gson = new Gson();
        String transaction = "ghsfh";
        double amount = 5432;
        VoucherGenerationRetryReq req = new VoucherGenerationRetryReq(transaction, amount);
        VoucherGenerationResp resp = new VoucherGenerationResp(transaction, "", VoucherService.RESPONSE_FAILED);

        String message = gson.toJson(req);
        Mockito.when(voucherService.retryCreateVoucher(transaction, amount)).thenThrow(new RetryLimitException());
        Acknowledgment ack = Mockito.mock(Acknowledgment.class);
        Mockito.when(ack.acknowledge()).thenReturn(null);
        listener.retryCreateVoucher(ack, message);
        Mockito.verify(messageQueue, Mockito.only()).sendMessage(DelayedPurchaseQueueListener.RESPONSE_VOUCHER_GENERATION, gson.toJson(resp));
    }

    private static Stream<Arguments> testCreateVoucherExceptionParameter() {
        String phoneNumber = "3123213";
        String transaction = "sdadsa";
        double amount = 56432;
        Purchase purchase = new Purchase(phoneNumber, transaction, amount);
        return Stream.of(
                Arguments.of(purchase, "dsa", Voucher.Status.success, VoucherService.RESPONSE_SUCCESS),
                Arguments.of(purchase, "", Voucher.Status.failed, VoucherService.RESPONSE_FAILED)
        );
    }
}
