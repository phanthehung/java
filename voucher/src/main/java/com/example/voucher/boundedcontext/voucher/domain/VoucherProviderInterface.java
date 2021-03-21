package com.example.voucher.boundedcontext.voucher.domain;

import com.example.voucher.boundedcontext.voucher.exception.TimeoutException;

public interface VoucherProviderInterface {

    String getVoucher(double amount, String transaction, boolean withRetry) throws TimeoutException;
}
