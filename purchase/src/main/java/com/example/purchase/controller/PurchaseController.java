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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value="/voucher")
public class PurchaseController {

    private PaymentServiceInterface paymentService;

    private PurchaseServiceInterface purchaseService;

    private VoucherServiceInterface voucherService;

    private SmsInterface smsService;

    private CacheInterface cache;

    private UniqueStringGenerator generator;

    public PurchaseController(PaymentServiceInterface paymentService, PurchaseServiceInterface purchaseService, VoucherServiceInterface voucherService, SmsInterface smsService, CacheInterface cache, UniqueStringGenerator generator) {
        this.paymentService = paymentService;
        this.purchaseService = purchaseService;
        this.voucherService = voucherService;
        this.smsService = smsService;
        this.cache = cache;
        this.generator = generator;
    }

    @PostMapping(value="/credit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PurchaseVoucherResp> purchaseVoucherByCreditCard(@RequestBody PurchaseVoucherReq request) throws UnsupportedEncodingException {
        String transaction = purchaseService.createPurchase(request.getPhoneNumber(), request.getCreditNumber(), request.getAmount());
        String url = this.paymentService.getCreditPaymentProviderUrl(
                new CreditPaymentRequest(request.getCreditNumber(), request.getExpireDate(), request.getSecretNumber(), transaction, request.getPaymentProvider(), request.getAmount())
        );

        return new ResponseEntity<>(
                new PurchaseVoucherResp(URLEncoder.encode(url, StandardCharsets.UTF_8.toString()), transaction),
                HttpStatus.OK
        );
    }


    @GetMapping(value = "token")
    public ResponseEntity<Void> sendToken(@RequestParam(name = "phone_number") String phoneNumber) {
        String token = generator.generateString(6);
        cache.put(token, phoneNumber, 300);
        smsService.sendMessage(phoneNumber, "token: " + token);
        return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "list")
    public ResponseEntity<List<Voucher>> getVouchersByPhoneNumber(@RequestParam(name = "phone_number") String phoneNumber, @RequestParam String token) {
        if (token == null || token.length() < 1) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        String cachedPhoneNumber = (String) cache.get(token, String.class);
        if (cachedPhoneNumber == null || !cachedPhoneNumber.equals(phoneNumber)) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        cache.remove(token);
        List<Voucher> vouchers = this.voucherService.getVoucherByPhoneNumber(phoneNumber);
        HashMap<String, Voucher> voucherMap = new HashMap<>();
        for (Voucher voucher : vouchers) {
            voucherMap.put(voucher.getTransaction(), voucher);
        }
        List<Voucher> response = new ArrayList<>();
        List<Purchase> purchases = this.purchaseService.getPurchaseByPhoneNumber(phoneNumber);
        for (Purchase purchase : purchases) {
            Voucher realVoucher = voucherMap.get(purchase.getTransaction());
            String voucherCode = realVoucher == null ? "" : realVoucher.getVoucherCode();
            response.add(new Voucher(purchase.getTransaction(), voucherCode, purchase.getAmount(), purchase.getStatus().toString()));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}