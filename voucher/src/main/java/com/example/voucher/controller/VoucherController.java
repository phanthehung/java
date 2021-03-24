package com.example.voucher.controller;

import com.example.voucher.boundedcontext.voucher.application.VoucherServiceInterface;
import com.example.voucher.boundedcontext.voucher.application.request.GenerateVoucherReq;
import com.example.voucher.boundedcontext.voucher.application.response.VoucherGenerationResp;
import com.example.voucher.boundedcontext.voucher.application.response.VoucherResp;
import com.example.voucher.boundedcontext.voucher.application.VoucherService;
import com.example.voucher.boundedcontext.voucher.domain.entity.Voucher;
import com.example.voucher.boundedcontext.voucher.exception.VoucherProcessingException;
import com.example.voucher.boundedcontext.voucher.exception.VoucherStatusConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/voucher")
public class VoucherController {

    private VoucherServiceInterface voucherService;

    public VoucherController(VoucherServiceInterface voucherService) {
        this.voucherService = voucherService;
    }

    @PostMapping
    public ResponseEntity<VoucherGenerationResp> purchaseVoucherByCreditCard(@RequestBody GenerateVoucherReq request) {
        try {
            String voucherCode = this.voucherService.generateVoucher(request.getTransaction(), request.getAmount(), request.getPhoneNumber());
            return new ResponseEntity<>(
                    new VoucherGenerationResp(request.getTransaction(), voucherCode, VoucherService.RESPONSE_SUCCESS),
                    HttpStatus.OK
            );
        } catch (VoucherStatusConflictException e) {
            Voucher v = e.getVoucher();
            int status = VoucherService.RESPONSE_SUCCESS;
            if (v.getStatus() == Voucher.Status.failed) {
                status = VoucherService.RESPONSE_FAILED;
            } else if (v.getStatus() == Voucher.Status.processing) {
                status = VoucherService.RESPONSE_PROCESSING;
            }
            return new ResponseEntity<>(
                    new VoucherGenerationResp(request.getTransaction(), v.getVoucherCode(), status),
                    HttpStatus.CONFLICT
            );
        } catch (VoucherProcessingException e) {
            return new ResponseEntity<>(
                    new VoucherGenerationResp(request.getTransaction(), "", VoucherService.RESPONSE_FAILED),
                    HttpStatus.ACCEPTED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new VoucherGenerationResp(request.getTransaction(), "", VoucherService.RESPONSE_FAILED),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping(value = "list")
    public ResponseEntity<List<VoucherResp>> getVouchersByPhoneNumber(@RequestParam(name = "phone_number") String phoneNumber) {
        List<Voucher> vouchers = this.voucherService.getVouchersByPhoneNumber(phoneNumber);
        List<VoucherResp> resp = new ArrayList<>();
        if (vouchers != null) {
            for (Voucher voucher : vouchers) {
                VoucherResp v = new VoucherResp(voucher.getTransaction(), voucher.getVoucherCode(), voucher.getStatus().toString(), voucher.getAmount());
                resp.add(v);
            }
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

}