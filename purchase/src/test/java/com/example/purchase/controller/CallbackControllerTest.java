package com.example.purchase.controller;

import com.example.purchase.boundedcontext.purchase.application.response.PurchaseCallbackCreditResp;
import com.example.purchase.boundedcontext.purchase.application.PaymentServiceInterface;
import com.example.purchase.boundedcontext.purchase.application.PurchaseServiceInterface;
import com.example.purchase.boundedcontext.purchase.domain.entity.Purchase;
import com.example.purchase.boundedcontext.purchase.exception.InvalidStateTransitionException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherCreationException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherProcessingException;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class CallbackControllerTest {

    @InjectMocks
    private CallbackController controller;

    @Mock
    private PurchaseServiceInterface purchaseService;

    @Mock
    private PaymentServiceInterface paymentService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPurchaseCallbackSuccess() throws InvalidStateTransitionException, VoucherProcessingException, ResourceAccessException, VoucherCreationException {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("transaction", "transaction");
        map.put("phone_number", "phone_number");
        map.put("success", true);
        map.put("amount", 1234);

        String decryptPayload = gson.toJson(map);
        Mockito.when(paymentService.decryptPaymentPayload(Mockito.anyString(), Mockito.anyString())).thenReturn(decryptPayload);
        Mockito.doNothing().when(purchaseService).confirmPurchase(Mockito.anyString());
        Mockito.when(purchaseService.createVoucher(Mockito.anyString())).thenReturn("voucher");

        ResponseEntity<PurchaseCallbackCreditResp> resp = controller.purchaseCallback(gson.toJson(map), "provider");
        Assertions.assertEquals("voucher", resp.getBody().getResponse());
    }

    @Test
    public void testPurchaseCallbackFail() throws InvalidStateTransitionException, VoucherProcessingException, ResourceAccessException, VoucherCreationException {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("transaction", "transaction");
        map.put("phone_number", "phone_number");
        map.put("success", false);
        map.put("amount", 1234);

        String decryptPayload = gson.toJson(map);
        Mockito.when(paymentService.decryptPaymentPayload(Mockito.anyString(), Mockito.anyString())).thenReturn(decryptPayload);

        ResponseEntity<PurchaseCallbackCreditResp> resp = controller.purchaseCallback(gson.toJson(map), "provider");
        Assertions.assertEquals("Payment failed, cannot purchase voucher", resp.getBody().getResponse());
    }

    @ParameterizedTest
    @MethodSource("testPurchaseCallbackExceptionParameter")
    public void testPurchaseCallbackException(Exception e, String message) throws InvalidStateTransitionException, VoucherProcessingException, ResourceAccessException, VoucherCreationException {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("transaction", "transaction");
        map.put("phone_number", "phone_number");
        map.put("success", true);
        map.put("amount", 1234);

        String decryptPayload = gson.toJson(map);
        Mockito.when(paymentService.decryptPaymentPayload(Mockito.anyString(), Mockito.anyString())).thenReturn(decryptPayload);
        Mockito.doNothing().when(purchaseService).confirmPurchase(Mockito.anyString());
        Mockito.when(purchaseService.createVoucher(Mockito.anyString())).thenThrow(e);

        ResponseEntity<PurchaseCallbackCreditResp> resp = controller.purchaseCallback(gson.toJson(map), "provider");
        Assertions.assertEquals(message, resp.getBody().getResponse());
    }

    private static Stream<Arguments> testPurchaseCallbackExceptionParameter() {
        ArrayList<Purchase> purchases = new ArrayList<>();
        Purchase purchase = new Purchase("1234", "dasd", "124", 231, Purchase.Status.success);
        purchases.add(purchase);
        return Stream.of(
                Arguments.of(new InvalidStateTransitionException(Purchase.Status.confirmed, Purchase.Status.pending, ""), "Something is wrong with this request, please contact administrator"),
                Arguments.of(new VoucherProcessingException(), "Your request will be processed within 30 seconds"),
                Arguments.of(new ResourceAccessException(""), "Cannot process request, please contact administrator")
        );
    }
}
