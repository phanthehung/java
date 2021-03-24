package com.example.purchase.boundedcontext.purchase.application;

import com.example.purchase.Util.UniqueStringGenerator;
import com.example.purchase.boundedcontext.purchase.application.PurchaseService;
import com.example.purchase.boundedcontext.purchase.application.VoucherServiceInterface;
import com.example.purchase.boundedcontext.purchase.domain.PurchaseRepositoryInterface;
import com.example.purchase.boundedcontext.purchase.domain.entity.Purchase;
import com.example.purchase.boundedcontext.purchase.exception.InvalidStateTransitionException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherCreationException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TestPurchaseService {

    @InjectMocks
    private PurchaseService purchaseService;

    @Mock
    private PurchaseRepositoryInterface purchaseRepository;

    @Mock
    private VoucherServiceInterface voucherService;

    @Mock
    private UniqueStringGenerator generator;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePurchase() {
        String phoneNumber = "123";
        String creditCard = "123";
        double amount = 123;
        String transaction = "unique";
        Mockito.when(generator.generateString(30)).thenReturn(transaction);
        Mockito.doNothing().when(purchaseRepository).persistPurchase(Mockito.any(Purchase.class));
        String result = this.purchaseService.createPurchase(phoneNumber, creditCard, amount);
        Assertions.assertEquals(result, transaction);
        Mockito.verify(purchaseRepository, Mockito.times(1)).persistPurchase(Mockito.any(Purchase.class));
    }

    @Test
    public void testConfirmPurchase() throws InvalidStateTransitionException {
        String phoneNumber = "123";
        double amount = 123;
        String transaction = "unique";

        Purchase purchase = new Purchase(transaction, phoneNumber, phoneNumber, amount, Purchase.Status.pending);
        Mockito.doNothing().when(purchaseRepository).updatePurchaseStatus(Mockito.any(Purchase.class));
        Mockito.when(purchaseRepository.getPurchaseInfo(transaction)).thenReturn(purchase);
        this.purchaseService.confirmPurchase(transaction);
        Assertions.assertEquals(Purchase.Status.confirmed, purchase.getStatus());
        Mockito.verify(purchaseRepository, Mockito.times(1)).updatePurchaseStatus(purchase);
    }

    @Test
    public void testCancelPurchase() throws InvalidStateTransitionException {
        String phoneNumber = "123";
        double amount = 123;
        String transaction = "unique";

        Purchase purchase = new Purchase(transaction, phoneNumber, phoneNumber, amount, Purchase.Status.pending);
        Mockito.doNothing().when(purchaseRepository).updatePurchaseStatus(Mockito.any(Purchase.class));
        Mockito.when(purchaseRepository.getPurchaseInfo(transaction)).thenReturn(purchase);
        this.purchaseService.cancelPurchase(transaction);
        Assertions.assertEquals(Purchase.Status.cancel, purchase.getStatus());
        Mockito.verify(purchaseRepository, Mockito.times(1)).updatePurchaseStatus(purchase);
    }

    @Test
    public void testMovePurchaseToSuccess() throws InvalidStateTransitionException {
        String phoneNumber = "123";
        double amount = 123;
        String transaction = "unique";

        Purchase purchase = new Purchase(transaction, phoneNumber, phoneNumber, amount, Purchase.Status.confirmed);
        Mockito.doNothing().when(purchaseRepository).updatePurchaseStatus(Mockito.any(Purchase.class));
        Mockito.when(purchaseRepository.getPurchaseInfo(transaction)).thenReturn(purchase);
        this.purchaseService.movePurchaseToSuccess(transaction);
        Assertions.assertEquals(Purchase.Status.success, purchase.getStatus());
        Mockito.verify(purchaseRepository, Mockito.times(1)).updatePurchaseStatus(purchase);
    }

    @Test
    public void testMovePurchaseToFailed() throws InvalidStateTransitionException {
        String phoneNumber = "123";
        double amount = 123;
        String transaction = "unique";

        Purchase purchase = new Purchase(transaction, phoneNumber, phoneNumber, amount, Purchase.Status.confirmed);
        Mockito.doNothing().when(purchaseRepository).updatePurchaseStatus(Mockito.any(Purchase.class));
        Mockito.when(purchaseRepository.getPurchaseInfo(transaction)).thenReturn(purchase);
        this.purchaseService.movePurchaseToFailed(transaction);
        Assertions.assertEquals(Purchase.Status.failed, purchase.getStatus());
        Mockito.verify(purchaseRepository, Mockito.times(1)).updatePurchaseStatus(purchase);
    }


    @Test
    public void testGetPurchaseByPhoneNumber() throws InvalidStateTransitionException {
        String phoneNumber = "123";
        double amount = 123;
        String transaction = "unique";

        List<Purchase> purchases = new ArrayList<>();
        purchases.add(new Purchase(transaction, phoneNumber, phoneNumber, amount, Purchase.Status.confirmed));
        Mockito.when(purchaseRepository.getPurchaseByPhoneNumber(phoneNumber)).thenReturn(purchases);
        List<Purchase> result = this.purchaseService.getPurchaseByPhoneNumber(phoneNumber);
        Assertions.assertEquals(purchases, result);
    }

    @ParameterizedTest
    @MethodSource("testStatusTransitionExceptionParameter")
    public void testStatusTransitionException(Purchase.Status current) {
        String phoneNumber = "123";
        double amount = 123;
        String transaction = "unique";

        Purchase purchase = new Purchase(transaction, phoneNumber, phoneNumber, amount, current);
        Mockito.doNothing().when(purchaseRepository).updatePurchaseStatus(Mockito.any(Purchase.class));
        Mockito.when(purchaseRepository.getPurchaseInfo(transaction)).thenReturn(purchase);
        Assertions.assertThrows(
                InvalidStateTransitionException.class,
                () -> purchaseService.movePurchaseToSuccess(transaction)
        );
    }


    @Test
    public void testPurchaseVoucher() throws VoucherProcessingException, VoucherCreationException, InvalidStateTransitionException {
        String phoneNumber = "123";
        double amount = 123;
        String transaction = "unique";
        String voucherCode = "voucher";

        Purchase purchase = new Purchase(transaction, phoneNumber, phoneNumber, amount, Purchase.Status.confirmed);
        Mockito.when(purchaseRepository.getPurchaseInfo(transaction)).thenReturn(purchase);
        Mockito.when(voucherService.createVoucher(transaction, amount, purchase.getPhoneNumber())).thenReturn(voucherCode);
        Mockito.doNothing().when(purchaseRepository).updatePurchaseStatus(Mockito.any(Purchase.class));

        String actual = purchaseService.createVoucher(transaction);
        Assertions.assertEquals(Purchase.Status.success, purchase.getStatus());
        Mockito.verify(purchaseRepository, Mockito.times(1)).updatePurchaseStatus(purchase);
        Assertions.assertEquals(voucherCode, actual);
    }

    @ParameterizedTest
    @MethodSource
    public void testPurchaseVoucherException(Purchase.Status status, Exception e) throws VoucherProcessingException, VoucherCreationException, InvalidStateTransitionException {
        String phoneNumber = "123";
        double amount = 123;
        String transaction = "unique";
        String voucherCode = "voucher";

        Purchase purchase = new Purchase(transaction, phoneNumber, phoneNumber, amount, Purchase.Status.confirmed);
        Mockito.when(purchaseRepository.getPurchaseInfo(transaction)).thenReturn(purchase);
        Mockito.when(voucherService.createVoucher(transaction, amount, purchase.getPhoneNumber())).thenThrow(e);
        Mockito.doNothing().when(purchaseRepository).updatePurchaseStatus(Mockito.any(Purchase.class));

        Assertions.assertThrows(
                e.getClass(),
                () -> purchaseService.createVoucher(transaction)
        );
        Assertions.assertEquals(status, purchase.getStatus());
        Mockito.verify(purchaseRepository, Mockito.times(1)).updatePurchaseStatus(purchase);

    }

    private static Stream<Arguments> testStatusTransitionExceptionParameter() {
        return Stream.of(
                Arguments.of(Purchase.Status.pending),
                Arguments.of(Purchase.Status.failed),
                Arguments.of(Purchase.Status.cancel)
        );
    }

    private static Stream<Arguments> testPurchaseVoucherException() {
        return Stream.of(
                Arguments.of(Purchase.Status.processing, new VoucherProcessingException()),
                Arguments.of(Purchase.Status.failed, new VoucherCreationException())
        );
    }
}
