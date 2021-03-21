package com.example.purchase.boundedcontext.purchase.domain;

import com.example.purchase.boundedcontext.purchase.domain.entity.Purchase;

import java.util.List;

public interface PurchaseRepositoryInterface {

    public void persistPurchase(Purchase purchase);

    public void updatePurchaseStatus(Purchase purchase);

    public Purchase getPurchaseInfo(String transaction);

    public List<Purchase> getPurchaseByPhoneNumber(String phoneNumber);
}
