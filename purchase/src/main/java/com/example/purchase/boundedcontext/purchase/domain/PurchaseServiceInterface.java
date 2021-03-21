package com.example.purchase.boundedcontext.purchase.domain;

import com.example.purchase.boundedcontext.purchase.domain.entity.Purchase;
import com.example.purchase.boundedcontext.purchase.exception.InvalidStateTransitionException;
import com.example.purchase.boundedcontext.purchase.exception.TimeoutException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherCreationException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherProcessingException;

import java.util.List;

public interface PurchaseServiceInterface {

    String createPurchase(String phoneNumber, String creditCardNumber, double amount);

    void confirmPurchase(String transaction) throws InvalidStateTransitionException;

    void cancelPurchase(String transaction) throws InvalidStateTransitionException;

    void movePurchaseToSuccess(String transaction) throws InvalidStateTransitionException;

    void movePurchaseToFailed(String transaction) throws InvalidStateTransitionException;

    String createVoucher(String transaction) throws InvalidStateTransitionException, VoucherCreationException, VoucherProcessingException, TimeoutException;

    List<Purchase> getPurchaseByPhoneNumber(String phoneNumber);
}
