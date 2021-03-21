package com.example.voucher.boundedcontext.voucher.exception;

import com.example.voucher.boundedcontext.voucher.domain.entity.Voucher;

public class VoucherStatusConflictException extends Exception {

    private Voucher voucher;

    public VoucherStatusConflictException(Voucher voucher) {
        this.voucher = voucher;
    }

    public Voucher getVoucher() {
        return voucher;
    }
}
