package com.example.purchase.controller;

import com.example.purchase.boundedcontext.purchase.application.request.PurchaseCallbackCreditPayload;
import com.example.purchase.boundedcontext.purchase.application.response.PurchaseCallbackCreditResp;
import com.example.purchase.boundedcontext.purchase.application.PaymentServiceInterface;
import com.example.purchase.boundedcontext.purchase.application.PurchaseServiceInterface;
import com.example.purchase.boundedcontext.purchase.exception.InvalidStateTransitionException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherProcessingException;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/callback/purchase")
public class CallbackController {

    private PurchaseServiceInterface purchaseService;
    private PaymentServiceInterface paymentService;

    public CallbackController(PurchaseServiceInterface purchaseService, PaymentServiceInterface paymentService) {
        this.purchaseService = purchaseService;
        this.paymentService = paymentService;
    }

    @GetMapping(value = "/credit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PurchaseCallbackCreditResp> purchaseCallback(@RequestParam("payload") String rawPayload, @RequestParam("provider") String provider) {
        Gson gson = new Gson();
        String decryptPayload = paymentService.decryptPaymentPayload(rawPayload, provider);
        PurchaseCallbackCreditPayload payload = gson.fromJson(decryptPayload, PurchaseCallbackCreditPayload.class);
        try {
            if (payload.isSuccess()) {
                purchaseService.confirmPurchase(payload.getTransaction());
                String voucher = purchaseService.createVoucher(payload.getTransaction());
                return new ResponseEntity<>(
                        new PurchaseCallbackCreditResp(voucher),
                        HttpStatus.OK
                );
            } else {
                purchaseService.cancelPurchase(payload.getTransaction());
                return new ResponseEntity<>(new PurchaseCallbackCreditResp("Payment failed, cannot purchase voucher"), HttpStatus.PAYMENT_REQUIRED);
            }
        } catch (InvalidStateTransitionException exception) {
            return new ResponseEntity<>(new PurchaseCallbackCreditResp("Something is wrong with this request, please contact administrator"), HttpStatus.CONFLICT);
        } catch (VoucherProcessingException e) {
            return new ResponseEntity<>(new PurchaseCallbackCreditResp("Your request will be processed within 30 seconds"), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new PurchaseCallbackCreditResp("Cannot process request, please contact administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
