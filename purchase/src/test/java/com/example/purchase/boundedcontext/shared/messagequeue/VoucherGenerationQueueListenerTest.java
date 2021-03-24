package com.example.purchase.boundedcontext.shared.messagequeue;

import com.example.purchase.boundedcontext.purchase.application.response.GenerateVoucherResp;
import com.example.purchase.boundedcontext.purchase.application.PurchaseServiceInterface;
import com.example.purchase.boundedcontext.purchase.exception.InvalidStateTransitionException;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Component;

@Component
public class VoucherGenerationQueueListenerTest {

    @InjectMocks
    private VoucherGenerationQueueListener queueListener;

    @Mock
    private PurchaseServiceInterface purchaseService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testVoucherGenerationEventSuccess() throws InvalidStateTransitionException {
        Gson gson = new Gson();
        Mockito.doNothing().when(purchaseService).movePurchaseToSuccess(Mockito.anyString());
        Mockito.doNothing().when(purchaseService).movePurchaseToFailed(Mockito.anyString());
        GenerateVoucherResp success = new GenerateVoucherResp("aaa", "aaa", GenerateVoucherResp.RESPONSE_SUCCESS);
        String message = gson.toJson(success);

        queueListener.voucherGenerationEvent(message);
        Mockito.verify(purchaseService, Mockito.only()).movePurchaseToSuccess(Mockito.anyString());
    }

    @Test
    public void testVoucherGenerationEventFail() throws InvalidStateTransitionException {
        Gson gson = new Gson();
        Mockito.doNothing().when(purchaseService).movePurchaseToFailed(Mockito.anyString());
        GenerateVoucherResp resp = new GenerateVoucherResp("aaa", "aaa", GenerateVoucherResp.RESPONSE_FAILED);
        String message = gson.toJson(resp);

        queueListener.voucherGenerationEvent(message);
        Mockito.verify(purchaseService, Mockito.only()).movePurchaseToFailed(Mockito.anyString());
    }

    @Test
    public void testVoucherGenerationEventProcessing() throws InvalidStateTransitionException {
        Gson gson = new Gson();
        GenerateVoucherResp resp = new GenerateVoucherResp("aaa", "aaa", GenerateVoucherResp.RESPONSE_PROCESSING);
        String message = gson.toJson(resp);

        queueListener.voucherGenerationEvent(message);
        Mockito.verify(purchaseService, Mockito.never()).movePurchaseToFailed(Mockito.anyString());
        Mockito.verify(purchaseService, Mockito.never()).movePurchaseToSuccess(Mockito.anyString());
    }

    @Test
    public void testVoucherGenerationEventException() {
        Gson gson = new Gson();
        GenerateVoucherResp resp = new GenerateVoucherResp("aaa", "aaa", 251);
        String message = gson.toJson(resp);


        Assertions.assertThrows(
                IllegalStateException.class,
                () -> queueListener.voucherGenerationEvent(message)
        );
    }

}
