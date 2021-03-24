package com.example.voucher.boundedcontext.voucher.application;

import com.example.voucher.boundedcontext.voucher.Event.VoucherCreationSuccessEvent;
import com.example.voucher.boundedcontext.voucher.application.VoucherService;
import com.example.voucher.boundedcontext.voucher.domain.VoucherProviderInterface;
import com.example.voucher.boundedcontext.voucher.domain.VoucherRepositoryInterface;
import com.example.voucher.boundedcontext.voucher.domain.entity.Voucher;
import com.example.voucher.boundedcontext.voucher.exception.RetryLimitException;
import com.example.voucher.boundedcontext.voucher.exception.TimeoutException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherProcessingException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherStatusConflictException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VoucherServiceTest {

    @InjectMocks
    private VoucherService voucherService;

    @Mock
    private VoucherRepositoryInterface voucherRepository;

    @Mock
    private VoucherProviderInterface voucherProvider;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRetryCreateVoucherMaxRetry() throws VoucherProcessingException, RetryLimitException {
        String transaction = "213";
        Voucher voucher = new Voucher(transaction, "213", 3123, 6, "321", LocalDateTime.now(), Voucher.Status.processing);
        Mockito.when(voucherRepository.getVoucherByTransaction(transaction)).thenReturn(voucher);

        Assertions.assertThrows(
                RetryLimitException.class,
                () -> voucherService.retryCreateVoucher(transaction, 321)
        );
    }

    @Test
    public void testRetryCreateVoucherSuccess() throws VoucherProcessingException, RetryLimitException, TimeoutException {
        String transaction = "213";
        String voucherCode = "tetwr";
        Voucher voucher = new Voucher(transaction, "213", 3123, 2, "321", LocalDateTime.now(), Voucher.Status.processing);

        Mockito.when(voucherRepository.getVoucherByTransaction(transaction)).thenReturn(voucher);
        Mockito.when(voucherProvider.getVoucher(123, transaction, false)).thenReturn(voucherCode);
        Mockito.doNothing().when(voucherRepository).updateVoucherCodeOnSuccess( transaction, voucherCode);
        Mockito.doNothing().when(eventPublisher).publishEvent( Mockito.any(VoucherCreationSuccessEvent.class));

        Assertions.assertEquals(voucherCode, voucherService.retryCreateVoucher(transaction, 123));
    }

    @Test
    public void testRetryCreateVoucherFail() throws VoucherProcessingException, RetryLimitException, TimeoutException {
        String transaction = "213";
        Voucher voucher = new Voucher(transaction, "213", 3123, 2, "321", LocalDateTime.now(), Voucher.Status.processing);

        Mockito.when(voucherRepository.getVoucherByTransaction(transaction)).thenReturn(voucher);
        Mockito.when(voucherProvider.getVoucher(123, transaction, false)).thenThrow(new TimeoutException());

        Assertions.assertThrows(
                VoucherProcessingException.class,
                () -> voucherService.retryCreateVoucher(transaction, 123)
        );
    }

    @Test
    public void testGetVouchersByPhoneNumber() {
        List<Voucher> vouchers = new ArrayList<>();
        Voucher voucher = new Voucher("transaction", "213", 3123, 2, "321", LocalDateTime.now(), Voucher.Status.processing);
        vouchers.add(voucher);
        Mockito.when(voucherRepository.getVouchersByPhoneNumber(Mockito.anyString())).thenReturn(vouchers);

        List<Voucher> actual = this.voucherService.getVouchersByPhoneNumber("phoneNumber");
        Assertions.assertEquals(vouchers, actual);
    }

    @Test
    public void testGenerateVoucherSuccess() throws VoucherStatusConflictException, VoucherProcessingException, TimeoutException {
        String transaction = "213";
        String voucherCode = "tetwr";
        String phoneNumber = "ooooooo";
        double amount = 3333;

        Mockito.when(voucherRepository.getVoucherByTransaction(transaction)).thenReturn(null);
        Mockito.when(voucherProvider.getVoucher(amount, transaction, true)).thenReturn(voucherCode);

        Assertions.assertEquals(voucherCode, voucherService.generateVoucher(transaction, amount, phoneNumber));

        Voucher voucher = voucherRepository.getVoucherByTransaction(transaction);
        if (voucher != null) {
            throw new VoucherStatusConflictException(voucher);
        }

        // intentionally fail this if cannot save empty voucher to db
        Voucher newVoucher = new Voucher(phoneNumber, transaction, amount, 0, "", LocalDateTime.now(), Voucher.Status.processing);
        voucherRepository.saveVoucher(newVoucher);
    }

    @Test
    public void testGenerateVoucherConflict() throws VoucherStatusConflictException, VoucherProcessingException, TimeoutException {
        String transaction = "213";
        String phoneNumber = "ooooooo";
        double amount = 3333;
        Voucher voucher = new Voucher("transaction", "213", 3123, 2, "321", LocalDateTime.now(), Voucher.Status.processing);

        Mockito.when(voucherRepository.getVoucherByTransaction(transaction)).thenReturn(voucher);

        Assertions.assertThrows(
                VoucherStatusConflictException.class,
                () -> voucherService.generateVoucher(transaction, amount, phoneNumber)
        );
    }

    @Test
    public void testGenerateVoucherException() throws VoucherStatusConflictException, VoucherProcessingException, TimeoutException {
        String transaction = "213";
        String voucherCode = "tetwr";
        String phoneNumber = "ooooooo";
        double amount = 3333;
        Mockito.when(voucherRepository.getVoucherByTransaction(transaction)).thenReturn(null);
        Mockito.when(voucherProvider.getVoucher(amount, transaction, true)).thenThrow(new TimeoutException());

        Assertions.assertThrows(
                VoucherProcessingException.class,
                () -> voucherService.generateVoucher(transaction, amount, phoneNumber)
        );
    }
}
