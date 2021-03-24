package com.example.purchase.boundedcontext.voucher;


import com.example.purchase.boundedcontext.purchase.application.response.GenerateVoucherResp;
import com.example.purchase.boundedcontext.purchase.exception.VoucherCreationException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherProcessingException;
import com.example.purchase.boundedcontext.shared.messagequeue.MessageQueueInterface;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class VoucherServiceTest {

    private static final String DELAYED_PURCHASE_QUEUE = "delayed_purchased_queue";

    @InjectMocks
    private VoucherService voucherService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MessageQueueInterface messageQueue;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateVoucher() throws VoucherCreationException, VoucherProcessingException {
        Gson gson = new Gson();
        GenerateVoucherResp resp = new GenerateVoucherResp("tran", "code", 202);
        ResponseEntity response = ResponseEntity.ok().body(gson.toJson(resp));
        Mockito.when(restTemplate.postForEntity(Mockito.any(String.class), Mockito.any(HttpEntity.class), Mockito.any())).thenReturn(response);

        String voucherCode = voucherService.createVoucher(resp.getTransaction(), 321, "phone");
        Assertions.assertEquals("code", voucherCode);
    }

    @ParameterizedTest
    @MethodSource("testCreateVoucherConflictParameter")
    public void testCreateVoucherConflict(Exception throwException, Exception returnException) {
        Gson gson = new Gson();
        Mockito.when(restTemplate.postForEntity(Mockito.any(String.class), Mockito.any(HttpEntity.class), Mockito.any())).thenThrow(throwException);

        Assertions.assertThrows(
                returnException.getClass(),
                () -> voucherService.createVoucher("123", 321, "phone")
        );
    }

    @Test
    public void testCreateVoucherProcessing() throws VoucherProcessingException, VoucherCreationException {
        Gson gson = new Gson();
        GenerateVoucherResp resp = new GenerateVoucherResp("tran", "code", 202);
        ResponseEntity response = ResponseEntity.accepted().body(gson.toJson(resp));
        Mockito.when(restTemplate.postForEntity(Mockito.any(String.class), Mockito.any(HttpEntity.class), Mockito.any())).thenReturn(response);

        Assertions.assertThrows(
                VoucherProcessingException.class,
                () -> voucherService.createVoucher("123", 321, "phone")
        );
    }

    @Test
    public void testCreateVoucherConflictSuccess() throws VoucherProcessingException, VoucherCreationException {
        Gson gson = new Gson();
        GenerateVoucherResp resp = new GenerateVoucherResp("tran", "code", 202);
        String voucherCode = "code";
        HttpStatusCodeException e = Mockito.mock(HttpStatusCodeException.class);
        Mockito.when(e.getResponseBodyAsString()).thenReturn(gson.toJson(new GenerateVoucherResp("aaa", voucherCode, GenerateVoucherResp.RESPONSE_SUCCESS)));
        Mockito.when(e.getStatusCode()).thenReturn(HttpStatus.CONFLICT);
        Mockito.doNothing().when(messageQueue).sendMessage(Mockito.anyString(), Mockito.anyString());

        Mockito.when(restTemplate.postForEntity(Mockito.any(String.class), Mockito.any(HttpEntity.class), Mockito.any()))
                .thenThrow(e);

        Assertions.assertEquals(voucherCode, voucherService.createVoucher("123", 321, "phone"));
    }

    private static Stream<Arguments> testCreateVoucherConflictParameter() {
        Gson gson = new Gson();
        HttpStatusCodeException failEx = Mockito.mock(HttpStatusCodeException.class);
        Mockito.when(failEx.getResponseBodyAsString()).thenReturn(gson.toJson(new GenerateVoucherResp("aaa", "bbb", GenerateVoucherResp.RESPONSE_FAILED)));
        Mockito.when(failEx.getStatusCode()).thenReturn(HttpStatus.CONFLICT);

        HttpStatusCodeException processingEx = Mockito.mock(HttpStatusCodeException.class);
        Mockito.when(processingEx.getResponseBodyAsString()).thenReturn(gson.toJson(new GenerateVoucherResp("aaa", "bbb", GenerateVoucherResp.RESPONSE_PROCESSING)));
        Mockito.when(processingEx.getStatusCode()).thenReturn(HttpStatus.CONFLICT);

        HttpStatusCodeException ex = Mockito.mock(HttpStatusCodeException.class);
        Mockito.when(ex.getResponseBodyAsString()).thenReturn(gson.toJson(new GenerateVoucherResp("aaa", "bbb", GenerateVoucherResp.RESPONSE_PROCESSING)));
        Mockito.when(ex.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        return Stream.of(
                Arguments.of(ex, new VoucherProcessingException()),
                Arguments.of(failEx, new VoucherCreationException()),
                Arguments.of(processingEx, new VoucherProcessingException()),
                Arguments.of(new ResourceAccessException(""), new VoucherProcessingException())
        );
    }

    @Test
    public void testGetVoucherByPhoneNumber() {
        Gson gson = new Gson();
        List<Voucher> vouchers = new ArrayList<>();
        Voucher voucher = new Voucher("321", "321", 123,"success");
        vouchers.add(voucher);

        ResponseEntity responseEntity = ResponseEntity.accepted().body(gson.toJson(vouchers));
        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any())).thenReturn(responseEntity);

        List<Voucher> actual = voucherService.getVoucherByPhoneNumber("312321");
        Assertions.assertEquals(gson.toJson(vouchers), gson.toJson(actual));
    }

    @Test
    public void testGetVoucherByPhoneNumberException() {
        HttpStatusCodeException ex = Mockito.mock(HttpStatusCodeException.class);
        Mockito.when(ex.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.any())).thenThrow(ex);

        List<Voucher> actual = voucherService.getVoucherByPhoneNumber("312321");
        Assertions.assertTrue(actual.isEmpty());
    }
}
