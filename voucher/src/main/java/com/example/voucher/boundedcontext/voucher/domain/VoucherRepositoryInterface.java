package com.example.voucher.boundedcontext.voucher.domain;

import com.example.voucher.boundedcontext.voucher.domain.entity.Voucher;

import java.util.List;

public interface VoucherRepositoryInterface {

    void saveVoucher(Voucher voucher);

    void updateVoucherCodeOnSuccess(String transaction, String voucherCode);

    void updateRetryCountByTransaction(String transaction);

    void setVoucherToFailed(String transaction);

    Voucher getVoucherByTransaction(String transaction);

    List<Voucher> getVouchersByPhoneNumber(String phoneNumber);
}
