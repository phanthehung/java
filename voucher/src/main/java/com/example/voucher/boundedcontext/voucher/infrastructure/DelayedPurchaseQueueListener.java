package com.example.voucher.boundedcontext.voucher.infrastructure;

import com.example.voucher.boundedcontext.voucher.application.VoucherServiceInterface;
import com.example.voucher.boundedcontext.voucher.application.request.VoucherGenerationRetryReq;
import com.example.voucher.boundedcontext.voucher.application.response.VoucherGenerationResp;
import com.example.voucher.boundedcontext.shared.messagequeue.MessageQueueInterface;
import com.example.voucher.boundedcontext.voucher.domain.VoucherService;
import com.example.voucher.boundedcontext.voucher.domain.entity.Purchase;
import com.example.voucher.boundedcontext.voucher.domain.entity.Voucher;
import com.example.voucher.boundedcontext.voucher.exception.RetryLimitException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherProcessingException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherStatusConflictException;
import com.google.gson.Gson;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class DelayedPurchaseQueueListener {

    private static final String DELAYED_PURCHASE_QUEUE = "delayed_purchased_queue";
    private static final String RESPONSE_VOUCHER_GENERATION = "response_voucher_generation";

    private VoucherServiceInterface voucherService;
    private MessageQueueInterface messageQueue;

    public DelayedPurchaseQueueListener(VoucherServiceInterface voucherService, MessageQueueInterface messageQueue) {
        this.voucherService = voucherService;
        this.messageQueue = messageQueue;
    }

    @SqsListener(value = DELAYED_PURCHASE_QUEUE, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void createVoucher(String message) {
        Gson gson = new Gson();
        Purchase purchase = gson.fromJson(message, Purchase.class);
        try {
            String voucherCode = voucherService.generateVoucher(purchase.getTransaction(), purchase.getAmount(), purchase.getPhoneNumber());
            messageQueue.sendMessage(
                    RESPONSE_VOUCHER_GENERATION,
                    gson.toJson(new VoucherGenerationResp(purchase.getTransaction(), voucherCode, VoucherService.RESPONSE_SUCCESS))
            );
        } catch (VoucherStatusConflictException e) {
            if (e.getVoucher().getStatus() == Voucher.Status.failed) {
                messageQueue.sendMessage(
                        RESPONSE_VOUCHER_GENERATION,
                        gson.toJson(new VoucherGenerationResp(purchase.getTransaction(), "", VoucherService.RESPONSE_FAILED))
                );
            } else if (e.getVoucher().getStatus() == Voucher.Status.success) {
                messageQueue.sendMessage(
                        RESPONSE_VOUCHER_GENERATION,
                        gson.toJson(new VoucherGenerationResp(purchase.getTransaction(), "", VoucherService.RESPONSE_SUCCESS))
                );
            }
        } catch (VoucherProcessingException ignored) {

        }
    }

    @SqsListener(value = VoucherService.VOUCHER_GENERATION_RETRY_QUEUE, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void retryCreateVoucher(String message) throws VoucherProcessingException {
        Gson gson = new Gson();
        VoucherGenerationRetryReq voucher = gson.fromJson(message, VoucherGenerationRetryReq.class);
        try {
            String voucherCode = voucherService.retryCreateVoucher(voucher.getTransaction(), voucher.getAmount());
            messageQueue.sendMessage(
                    RESPONSE_VOUCHER_GENERATION,
                    gson.toJson(new VoucherGenerationResp(voucher.getTransaction(), voucherCode, VoucherService.RESPONSE_SUCCESS))
            );
        } catch (RetryLimitException e) {
            messageQueue.sendMessage(
                    RESPONSE_VOUCHER_GENERATION,
                    gson.toJson(new VoucherGenerationResp(voucher.getTransaction(), "", VoucherService.RESPONSE_FAILED))
            );
        }
    }
}
