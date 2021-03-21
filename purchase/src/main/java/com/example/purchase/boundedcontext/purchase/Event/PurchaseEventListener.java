package com.example.purchase.boundedcontext.purchase.Event;

import com.example.purchase.boundedcontext.purchase.domain.VoucherServiceInterface;
import com.example.purchase.boundedcontext.shared.sms.SmsInterface;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class PurchaseEventListener {

    private SmsInterface smsService;

    private VoucherServiceInterface voucherService;

    public PurchaseEventListener(SmsInterface smsService, VoucherServiceInterface voucherService) {
        this.smsService = smsService;
        this.voucherService = voucherService;
    }

    @EventListener
    public void voucherGenerationListener(VoucherCreationSuccessEvent event){
//        this.voucherService.getVoucherByPhoneNumber()
    }
}
