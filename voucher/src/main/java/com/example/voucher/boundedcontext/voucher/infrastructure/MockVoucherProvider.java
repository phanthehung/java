package com.example.voucher.boundedcontext.voucher.infrastructure;

import com.example.voucher.Util.RandomStringGenerator;
import com.example.voucher.boundedcontext.shared.messagequeue.MessageQueueInterface;
import com.example.voucher.boundedcontext.voucher.application.request.VoucherGenerationRetryReq;
import com.example.voucher.boundedcontext.voucher.domain.VoucherProviderInterface;
import com.example.voucher.boundedcontext.voucher.domain.VoucherService;
import com.example.voucher.boundedcontext.voucher.exception.TimeoutException;
import com.google.gson.Gson;

import java.util.Random;

public class MockVoucherProvider implements VoucherProviderInterface {

    private MessageQueueInterface messageQueue;

    public MockVoucherProvider(MessageQueueInterface messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public String getVoucher(double amount, String transaction, boolean withRetry) throws TimeoutException {
        try {
            Random rand = new Random();
            int next = rand.nextInt(50);
            if (next % 2 == 0) {
                return RandomStringGenerator.generateString(20);
            } else {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            if (withRetry) {
                Gson gson = new Gson();
                messageQueue.sendMessage(VoucherService.VOUCHER_GENERATION_RETRY_QUEUE, gson.toJson(new VoucherGenerationRetryReq(transaction, amount)));
            }
            throw e;
        }
    }
}
