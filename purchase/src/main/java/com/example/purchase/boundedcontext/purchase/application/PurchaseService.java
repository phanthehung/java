package com.example.purchase.boundedcontext.purchase.application;

import com.example.purchase.Util.UniqueStringGenerator;
import com.example.purchase.boundedcontext.purchase.domain.PurchaseRepositoryInterface;
import com.example.purchase.boundedcontext.purchase.domain.entity.Purchase;
import com.example.purchase.boundedcontext.purchase.exception.InvalidStateTransitionException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherCreationException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherProcessingException;

import java.util.List;

public class PurchaseService implements PurchaseServiceInterface {

    private PurchaseRepositoryInterface purchaseRepository;

    private VoucherServiceInterface voucherService;

    private UniqueStringGenerator generator;

    public PurchaseService(PurchaseRepositoryInterface purchaseRepository, VoucherServiceInterface voucherService, UniqueStringGenerator generator) {
        this.purchaseRepository = purchaseRepository;
        this.voucherService = voucherService;
        this.generator = generator;
    }

    @Override
    public String createPurchase(String phoneNumber, String creditCardNumber, double amount) {
        String transaction = generator.generateString(30);
        Purchase purchase = new Purchase(phoneNumber, transaction, creditCardNumber, amount, Purchase.Status.pending);
        purchaseRepository.persistPurchase(purchase);
        return transaction;
    }

    @Override
    public void confirmPurchase(String transaction) throws InvalidStateTransitionException {
        this.movePurchaseToStatus(transaction, Purchase.Status.confirmed);
    }

    @Override
    public void cancelPurchase(String transaction) throws InvalidStateTransitionException {
        this.movePurchaseToStatus(transaction, Purchase.Status.cancel);
    }

    @Override
    public void movePurchaseToSuccess(String transaction) throws InvalidStateTransitionException {
        this.movePurchaseToStatus(transaction, Purchase.Status.success);
    }

    @Override
    public void movePurchaseToFailed(String transaction) throws InvalidStateTransitionException {
        this.movePurchaseToStatus(transaction, Purchase.Status.failed);
    }

    @Override
    public String createVoucher(String transaction) throws InvalidStateTransitionException, VoucherCreationException, VoucherProcessingException {
        Purchase purchase = purchaseRepository.getPurchaseInfo(transaction);
        try {
            String voucher = voucherService.createVoucher(transaction, purchase.getAmount(), purchase.getPhoneNumber());
            this.movePurchaseToSuccess(transaction);
            return voucher;
        } catch (VoucherProcessingException e) {
            purchase.transitToNextStatus(Purchase.Status.processing);
            purchaseRepository.updatePurchaseStatus(purchase);
            throw e;
        } catch (VoucherCreationException e) {
            this.movePurchaseToFailed(transaction);
            throw e;
        }
    }

    @Override
    public List<Purchase> getPurchaseByPhoneNumber(String phoneNumber) {
        return this.purchaseRepository.getPurchaseByPhoneNumber(phoneNumber);
    }

    private void movePurchaseToStatus(String transaction, Purchase.Status next) throws InvalidStateTransitionException {
        Purchase purchase = purchaseRepository.getPurchaseInfo(transaction);
        purchase.transitToNextStatus(next);
        purchaseRepository.updatePurchaseStatus(purchase);
    }
}
