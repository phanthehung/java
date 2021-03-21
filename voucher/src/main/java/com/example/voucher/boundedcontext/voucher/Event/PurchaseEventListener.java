package com.example.voucher.boundedcontext.voucher.Event;

import com.example.voucher.boundedcontext.shared.sms.SmsInterface;
import com.example.voucher.boundedcontext.voucher.application.VoucherServiceInterface;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PurchaseEventListener {

    private SmsInterface smsService;

    public PurchaseEventListener(SmsInterface smsService, VoucherServiceInterface voucherService) {
        this.smsService = smsService;
    }

    @EventListener
    public void voucherGenerationListener(VoucherCreationSuccessEvent event){
        String message = "transaction: " + event.getTransaction() + " | phone number: " + event.getPhoneNumber() + " | amount: " + event.getAmount();
        smsService.sendMessage(event.getPhoneNumber(), message);
    }
}
