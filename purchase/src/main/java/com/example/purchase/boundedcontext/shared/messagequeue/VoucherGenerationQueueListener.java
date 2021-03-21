package com.example.purchase.boundedcontext.shared.messagequeue;

import com.example.purchase.boundedcontext.purchase.application.response.GenerateVoucherResp;
import com.example.purchase.boundedcontext.purchase.domain.PurchaseServiceInterface;
import com.example.purchase.boundedcontext.purchase.exception.InvalidStateTransitionException;
import com.google.gson.Gson;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class VoucherGenerationQueueListener {

    private static final String RESPONSE_VOUCHER_GENERATION = "response_voucher_generation";



    PurchaseServiceInterface purchaseService;

    public VoucherGenerationQueueListener(PurchaseServiceInterface purchaseService) {
        this.purchaseService = purchaseService;
    }

    @SqsListener(value = RESPONSE_VOUCHER_GENERATION, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void voucherGenerationEvent(String message) throws InvalidStateTransitionException {
        Gson gson = new Gson();
        GenerateVoucherResp resp = gson.fromJson(message, GenerateVoucherResp.class);
        switch (resp.getCode()) {
            case GenerateVoucherResp.RESPONSE_FAILED:
                purchaseService.movePurchaseToFailed(resp.getTransaction());
                break;
            case GenerateVoucherResp.RESPONSE_SUCCESS:
                purchaseService.movePurchaseToSuccess(resp.getTransaction());
                break;
            case GenerateVoucherResp.RESPONSE_PROCESSING:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + resp.getCode());
        }
    }

}
