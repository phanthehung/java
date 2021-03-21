package com.example.purchase.boundedcontext.purchase.domain;

import com.example.purchase.boundedcontext.purchase.exception.VoucherCreationException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherProcessingException;
import com.example.purchase.boundedcontext.voucher.Voucher;

import java.util.List;

public interface VoucherServiceInterface {

    public String createVoucher(String transaction, double amount, String phoneNumber) throws VoucherCreationException, VoucherProcessingException;

    public List<Voucher> getVoucherByPhoneNumber(String phoneNumber);
}
