package com.example.voucher.boundedcontext.voucher.infrastructure;

import com.example.voucher.boundedcontext.voucher.domain.entity.Voucher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;

public class VoucherRepositoryTest {

    @InjectMocks
    private VoucherRepository voucherRepository;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveVoucher() {
        Voucher voucher = new Voucher();

        Mockito.doNothing().when(entityManager).persist(Mockito.any(Voucher.class));
        entityManager.persist(voucher);
        Mockito.verify(entityManager, Mockito.only()).persist(Mockito.any(Voucher.class));
    }

    @Test
    public void testUpdateVoucherCodeOnSuccess() {
        String transaction = "2133";
        String voucherCode = "gasgsa";
        String query = "UPDATE Voucher v set v.voucherCode = :voucherCode, v.status = :status  WHERE v.transaction = :transaction";

        Query mockQuery = Mockito.mock(Query.class);

        Mockito.when(this.entityManager.createQuery(query)).thenReturn(mockQuery);
        Mockito.when(mockQuery.setParameter("transaction", transaction)).thenReturn(mockQuery);
        Mockito.when(mockQuery.setParameter("voucherCode", voucherCode)).thenReturn(mockQuery);
        Mockito.when(mockQuery.setParameter("status", Voucher.Status.success)).thenReturn(mockQuery);
        Mockito.when(mockQuery.executeUpdate()).thenReturn(1);

        this.voucherRepository.updateVoucherCodeOnSuccess(transaction, voucherCode);

        Mockito.verify(entityManager, Mockito.times(1)).createQuery(query);
        Mockito.verify(mockQuery, Mockito.times(3)).setParameter(Mockito.anyString(), Mockito.any());
        Mockito.verify(mockQuery, Mockito.times(1)).executeUpdate();
    }

    @Test
    public void testUpdateRetryCountByTransaction() {
        String transaction = "2133";
        String query = "UPDATE Voucher v set v.retry = v.retry + 1 WHERE v.transaction = :transaction";

        Query mockQuery = Mockito.mock(Query.class);

        Mockito.when(this.entityManager.createQuery(query)).thenReturn(mockQuery);
        Mockito.when(mockQuery.setParameter("transaction", transaction)).thenReturn(mockQuery);
        Mockito.when(mockQuery.executeUpdate()).thenReturn(1);

        this.voucherRepository.updateRetryCountByTransaction(transaction);

        Mockito.verify(entityManager, Mockito.times(1)).createQuery(query);
        Mockito.verify(mockQuery, Mockito.times(1)).setParameter(Mockito.anyString(), Mockito.any());
        Mockito.verify(mockQuery, Mockito.times(1)).executeUpdate();
    }

    @Test
    public void testSetVoucherToFailed() {
        String transaction = "2133";
        String query = "UPDATE Voucher v set v.status = :status WHERE v.transaction = :transaction";

        Query mockQuery = Mockito.mock(Query.class);

        Mockito.when(this.entityManager.createQuery(query)).thenReturn(mockQuery);
        Mockito.when(mockQuery.setParameter("transaction", transaction)).thenReturn(mockQuery);
        Mockito.when(mockQuery.setParameter("status", Voucher.Status.failed)).thenReturn(mockQuery);
        Mockito.when(mockQuery.executeUpdate()).thenReturn(1);

        this.voucherRepository.setVoucherToFailed(transaction);

        Mockito.verify(entityManager, Mockito.times(1)).createQuery(query);
        Mockito.verify(mockQuery, Mockito.times(2)).setParameter(Mockito.anyString(), Mockito.any());
        Mockito.verify(mockQuery, Mockito.times(1)).executeUpdate();
    }

    @Test
    public void testGetVoucherByTransaction() {
        String transaction = "2133";
        String query = "SELECT v from Voucher v WHERE v.transaction = :transaction";
        Voucher voucher = new Voucher();
        TypedQuery<Voucher> mockTypedQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(this.entityManager.createQuery(query, Voucher.class)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter("transaction", transaction)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.getSingleResult()).thenReturn(voucher);

        Assertions.assertEquals(voucher, this.voucherRepository.getVoucherByTransaction(transaction));
    }

    @Test
    public void testGetVoucherByTransactionException() {
        String transaction = "2133";
        String query = "SELECT v from Voucher v WHERE v.transaction = :transaction";
        TypedQuery<Voucher> mockTypedQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(this.entityManager.createQuery(query, Voucher.class)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter("transaction", transaction)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        Assertions.assertEquals(null, voucherRepository.getVoucherByTransaction(transaction));
    }

    @Test
    public void testGetVouchersByPhoneNumber() {
        String phoneNumber = "2133";
        String query = "SELECT v from Voucher v WHERE v.phoneNumber = :phoneNumber";
        Voucher voucher = new Voucher();
        ArrayList vouchers = new ArrayList();
        vouchers.add(voucher);
        TypedQuery<Voucher> mockTypedQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(this.entityManager.createQuery(query, Voucher.class)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.setParameter("phoneNumber", phoneNumber)).thenReturn(mockTypedQuery);
        Mockito.when(mockTypedQuery.getResultList()).thenReturn(vouchers);

        Assertions.assertEquals(vouchers, this.voucherRepository.getVouchersByPhoneNumber(phoneNumber));
    }
}
