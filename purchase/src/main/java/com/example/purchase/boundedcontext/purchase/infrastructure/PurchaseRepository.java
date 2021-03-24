package com.example.purchase.boundedcontext.purchase.infrastructure;

import com.example.purchase.boundedcontext.purchase.domain.PurchaseRepositoryInterface;
import com.example.purchase.boundedcontext.purchase.domain.entity.Purchase;
import com.example.purchase.boundedcontext.purchase.domain.entity.PurchaseHistory;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseRepository implements PurchaseRepositoryInterface {

    private EntityManager entityManager;

    public PurchaseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void persistPurchase(Purchase purchase) {
        LocalDateTime now = LocalDateTime.now();
        entityManager.persist(purchase);
        PurchaseHistory history = new PurchaseHistory(
                purchase.getTransaction(),
                purchase.getStatus().toString(),
                now
        );
        entityManager.persist(history);
    }

    @Override
    @Transactional
    public void updatePurchaseStatus(Purchase purchase) {
        LocalDateTime now = LocalDateTime.now();
        entityManager.createQuery(
                "UPDATE Purchase p set p.status = :status  WHERE p.transaction = :transaction"
        )
                .setParameter("status", purchase.getStatus())
                .setParameter("transaction", purchase.getTransaction())
                .executeUpdate();
        PurchaseHistory history = new PurchaseHistory(
                purchase.getTransaction(),
                purchase.getStatus().toString(),
                now
        );
        entityManager.persist(history);
    }

    @Override
    @Transactional
    public Purchase getPurchaseInfo(String transaction) {
        return entityManager.createQuery(
                "SELECT p from Purchase p WHERE p.transaction = :transaction", Purchase.class).
                setParameter("transaction", transaction).getSingleResult();
    }

    @Override
    public List<Purchase> getPurchaseByPhoneNumber(String phoneNumber) {
        return entityManager.createQuery(
                "SELECT p from Purchase p WHERE p.phoneNumber = :phoneNumber", Purchase.class).
                setParameter("phoneNumber", phoneNumber).getResultList();
    }
}
