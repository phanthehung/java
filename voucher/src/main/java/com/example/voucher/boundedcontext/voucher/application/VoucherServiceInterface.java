package com.example.voucher.boundedcontext.voucher.application;

import com.example.voucher.boundedcontext.voucher.domain.entity.Voucher;
import com.example.voucher.boundedcontext.voucher.exception.RetryLimitException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherProcessingException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherStatusConflictException;

import java.util.List;

public interface VoucherServiceInterface {

    public String generateVoucher(String transaction, double amount, String phoneNumber) throws VoucherStatusConflictException, VoucherProcessingException;

    String retryCreateVoucher(String transaction, double amount) throws VoucherProcessingException, RetryLimitException;

    public List<Voucher> getVouchersByPhoneNumber(String phoneNumber);
}
