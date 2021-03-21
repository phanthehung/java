package com.example.voucher.boundedcontext.voucher.domain;

import com.example.voucher.boundedcontext.voucher.Event.VoucherCreationSuccessEvent;
import com.example.voucher.boundedcontext.voucher.application.VoucherServiceInterface;
import com.example.voucher.boundedcontext.voucher.domain.entity.Voucher;
import com.example.voucher.boundedcontext.voucher.exception.RetryLimitException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherProcessingException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherStatusConflictException;
import com.google.gson.Gson;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class VoucherService implements VoucherServiceInterface {

    public static final String VOUCHER_GENERATION_RETRY_QUEUE = "voucher_generation_retry_queue";
    public static final int RESPONSE_PROCESSING = 100;
    public static final int RESPONSE_SUCCESS = 101;
    public static final int RESPONSE_FAILED = 102;

    private VoucherRepositoryInterface voucherRepository;
    private VoucherProviderInterface voucherProvider;
    private ApplicationEventPublisher eventPublisher;

    public VoucherService(VoucherRepositoryInterface voucherRepository, VoucherProviderInterface voucherProvider, ApplicationEventPublisher eventPublisher) {
        this.voucherRepository = voucherRepository;
        this.voucherProvider = voucherProvider;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public String retryCreateVoucher(String transaction, double amount) throws VoucherProcessingException, RetryLimitException {
        Voucher voucher = voucherRepository.getVoucherByTransaction(transaction);
        if (voucher.getRetry() > 5) {
            voucherRepository.setVoucherToFailed(transaction);
            throw new RetryLimitException();
        }
        try {
            String voucherCode = voucherProvider.getVoucher(amount, transaction, false);
            voucherRepository.updateVoucherCodeOnSuccess(transaction, voucherCode);
            eventPublisher.publishEvent(new VoucherCreationSuccessEvent(this, transaction, voucher.getPhoneNumber(), voucherCode, amount));
            return voucherCode;
        } catch (Exception e) {
            voucherRepository.updateRetryCountByTransaction(transaction);
            throw new VoucherProcessingException(e.getMessage());
        }
    }

    @Override
    public List<Voucher> getVouchersByPhoneNumber(String phoneNumber) {
        List<Voucher> vouchers = this.voucherRepository.getVouchersByPhoneNumber(phoneNumber);
        if (!vouchers.isEmpty()) {
           return vouchers;
        }
        return null;
    }

    @Override
    public String generateVoucher(String transaction, double amount, String phoneNumber) throws VoucherStatusConflictException, VoucherProcessingException {
        Voucher voucher = voucherRepository.getVoucherByTransaction(transaction);
        if (voucher != null) {
            throw new VoucherStatusConflictException(voucher);
        }

        // intentionally fail this if cannot save empty voucher to db
        Voucher newVoucher = new Voucher(phoneNumber, transaction, amount, 0, "", LocalDateTime.now(), Voucher.Status.processing);
        voucherRepository.saveVoucher(newVoucher);

        try {
            String voucherCode = voucherProvider.getVoucher(amount, transaction, true);
            voucherRepository.updateVoucherCodeOnSuccess(transaction, voucherCode);
            eventPublisher.publishEvent(new VoucherCreationSuccessEvent(this, transaction, phoneNumber, voucherCode, amount));
            return voucherCode;
        } catch (Exception e) {
            throw new VoucherProcessingException(e.getMessage());
        }
    }
}
