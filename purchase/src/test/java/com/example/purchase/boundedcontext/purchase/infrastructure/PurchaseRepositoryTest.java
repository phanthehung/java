package com.example.purchase.boundedcontext.purchase.infrastructure;

import com.example.purchase.boundedcontext.purchase.domain.entity.Purchase;
import com.example.purchase.boundedcontext.purchase.domain.entity.PurchaseHistory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class PurchaseRepositoryTest {

    @InjectMocks
    private PurchaseRepository purchaseRepository;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPersistPurchase() {
        Purchase purchase = new Purchase("122", "1", "", 1, Purchase.Status.pending);
        Mockito.doNothing().when(entityManager).persist(purchase);
        Mockito.doNothing().when(entityManager).persist(Mockito.any(PurchaseHistory.class));
        this.purchaseRepository.persistPurchase(purchase);
        Mockito.verify(entityManager, Mockito.times(1)).persist(purchase);
        Mockito.verify(entityManager, Mockito.times(1)).persist(Mockito.any(PurchaseHistory.class));
    }

    @Test
    public void testUpdatePurchaseStatus() {
        String query =  "UPDATE Purchase p set p.status = :status  WHERE p.transaction = :transaction";
        Purchase purchase = new Purchase("123", "123", "123", 123, Purchase.Status.pending);
        TypedQuery<Purchase> mockTypedQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(this.entityManager.createQuery(query)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter("status", purchase.getStatus())).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter("transaction", purchase.getTransaction())).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.executeUpdate()).thenReturn(1);
        Mockito.doNothing().when(entityManager).persist(Mockito.any());

        this.purchaseRepository.updatePurchaseStatus(purchase);
        Mockito.verify(entityManager, Mockito.times(1)).createQuery(query);
        Mockito.verify(entityManager, Mockito.times(1)).persist(Mockito.any());

    }

    @Test
    public void testGetPurchaseInfo() {
        String transaction = "2133";
        String query = "SELECT p from Purchase p WHERE p.transaction = :transaction";
        Purchase purchase = new Purchase();
        TypedQuery<Purchase> mockTypedQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(
                this.entityManager.createQuery(query, Purchase.class)
        )
                .thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter("transaction", transaction)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.getSingleResult()).thenReturn(purchase);

        Purchase actual = this.purchaseRepository.getPurchaseInfo(transaction);
        Mockito.verify(entityManager, Mockito.times(1)).createQuery(query, Purchase.class);
        Assertions.assertEquals(actual, purchase);
    }

    @Test
    public void testGetPurchaseByPhoneNumber() {
        String phoneNumber = "2133";
        String query = "SELECT p from Purchase p WHERE p.phoneNumber = :phoneNumber";
        Purchase purchase = new Purchase();
        ArrayList<Purchase> purchases = new ArrayList<>();
        purchases.add(purchase);
        TypedQuery<Purchase> mockTypedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(
                this.entityManager.createQuery(query, Purchase.class)
        )
                .thenReturn(mockTypedQuery);

        Mockito.when(mockTypedQuery.setParameter("phoneNumber", phoneNumber)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.getResultList()).thenReturn(purchases);

        List<Purchase> actual = this.purchaseRepository.getPurchaseByPhoneNumber(phoneNumber);
        Mockito.verify(entityManager, Mockito.times(1)).createQuery(query, Purchase.class);
        Assertions.assertEquals(actual, purchases);
    }
}
