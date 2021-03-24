package com.example.purchase.controller;

import com.example.purchase.Util.UniqueStringGenerator;
import com.example.purchase.boundedcontext.payment.CreditPaymentRequest;
import com.example.purchase.boundedcontext.purchase.application.request.PurchaseVoucherReq;
import com.example.purchase.boundedcontext.purchase.application.response.PurchaseVoucherResp;
import com.example.purchase.boundedcontext.purchase.application.PaymentServiceInterface;
import com.example.purchase.boundedcontext.purchase.application.PurchaseServiceInterface;
import com.example.purchase.boundedcontext.purchase.application.VoucherServiceInterface;
import com.example.purchase.boundedcontext.purchase.domain.entity.Purchase;
import com.example.purchase.boundedcontext.shared.cache.CacheInterface;
import com.example.purchase.boundedcontext.shared.sms.SmsInterface;
import com.example.purchase.boundedcontext.voucher.Voucher;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PurchaseControllerTest {

    @InjectMocks
    private PurchaseController purchaseController;

    @Mock
    private PaymentServiceInterface paymentService;

    @Mock
    private PurchaseServiceInterface purchaseService;

    @Mock
    private VoucherServiceInterface voucherService;

    @Mock
    private SmsInterface smsService;

    @Mock
    private CacheInterface cache;

    @Mock
    private UniqueStringGenerator generator;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPurchaseVoucherByCreditCard() throws Exception {
        String transaction = "r23";
        String url = "5125";
        ResponseEntity<PurchaseVoucherResp> expected = new ResponseEntity<>(
                new PurchaseVoucherResp(URLEncoder.encode(url, StandardCharsets.UTF_8.toString()), transaction),
                HttpStatus.OK
        );
        PurchaseVoucherReq request = new PurchaseVoucherReq("231", "123", "123", "123", "421", 123);
        CreditPaymentRequest creditPaymentRequest = new CreditPaymentRequest(request.getCreditNumber(), request.getExpireDate(), request.getSecretNumber(), transaction, request.getPaymentProvider(), request.getAmount());
        Mockito.when(purchaseService.createPurchase(request.getPhoneNumber(), request.getCreditNumber(), request.getAmount())).thenReturn(transaction);
        Mockito.when(paymentService.getCreditPaymentProviderUrl(Mockito.any(CreditPaymentRequest.class))).thenReturn(url);

        ResponseEntity<PurchaseVoucherResp> actual = purchaseController.purchaseVoucherByCreditCard(request);
        Assertions.assertEquals(expected.getBody().getUrl(), actual.getBody().getUrl());
    }

    @Test
    public void testSendToken() {
        String phoneNumber = "31321";
        String token = "123456";
        Mockito.when(generator.generateString(6)).thenReturn(token);
        Mockito.doNothing().when(cache).put(phoneNumber, token, 300);
        Mockito.doNothing().when(smsService).sendMessage(Mockito.anyString(), Mockito.anyString());

        ResponseEntity<Void> actual = purchaseController.sendToken(phoneNumber);
        Assertions.assertEquals(HttpStatus.ACCEPTED, actual.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("testGetVouchersByPhoneNumberParameter")
    public void testGetVouchersByPhoneNumber(String phoneNumber, String token, HttpStatus code, List<Purchase> purchases) {

        List<Voucher> vouchers = new ArrayList<>();
        Voucher voucher = new Voucher("dasd", "gagas", 231, Purchase.Status.success.toString());
        vouchers.add(voucher);
        Mockito.when(voucherService.getVoucherByPhoneNumber(phoneNumber)).thenReturn(vouchers);
        Mockito.when(cache.get("4321", String.class)).thenReturn("1234");
        Mockito.doNothing().when(cache).remove(token);
        Mockito.when(purchaseService.getPurchaseByPhoneNumber(phoneNumber)).thenReturn(purchases);


        ResponseEntity<List<Voucher>> actual = purchaseController.getVouchersByPhoneNumber(phoneNumber, token);
        Gson gson = new Gson();

        Assertions.assertEquals(code, actual.getStatusCode());
        if (HttpStatus.OK == code) {
            Assertions.assertEquals(gson.toJson(vouchers), gson.toJson(actual.getBody()));
        }
    }

    private static Stream<Arguments> testGetVouchersByPhoneNumberParameter() {
        ArrayList<Purchase> purchases = new ArrayList<>();
        Purchase purchase = new Purchase("1234", "dasd", "124", 231, Purchase.Status.success);
        purchases.add(purchase);
        return Stream.of(
                Arguments.of("432", "", HttpStatus.FORBIDDEN, new ArrayList<>()),
                Arguments.of("432", "4321", HttpStatus.FORBIDDEN, new ArrayList<>()),
                Arguments.of("", "4321", HttpStatus.FORBIDDEN, new ArrayList<>()),
                Arguments.of("32", "4321", HttpStatus.FORBIDDEN, new ArrayList<>()),
                Arguments.of("1234", "4321", HttpStatus.OK, purchases)
        );
    }
}