package com.example.purchase.boundedcontext.voucher;


import com.example.purchase.boundedcontext.purchase.application.response.GenerateVoucherResp;
import com.example.purchase.boundedcontext.purchase.domain.VoucherServiceInterface;
import com.example.purchase.boundedcontext.purchase.exception.TimeoutException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherCreationException;
import com.example.purchase.boundedcontext.purchase.exception.VoucherProcessingException;
import com.example.purchase.boundedcontext.shared.messagequeue.MessageQueueInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.*;

public class VoucherService implements VoucherServiceInterface {

    private static final String DELAYED_PURCHASE_QUEUE = "delayed_purchased_queue";

    private RestTemplate restTemplate;

    private MessageQueueInterface messageQueue;

    public VoucherService(RestTemplate restTemplate, MessageQueueInterface messageQueue) {
        this.restTemplate = restTemplate;
        this.messageQueue = messageQueue;
    }

    @Override
    public String createVoucher(String transaction, double amount, String phoneNumber) throws VoucherCreationException, VoucherProcessingException {
        String domain = System.getenv("VOUCHER_SERVICE_URL");
        if (domain == null || domain.equals("")) {
            domain = "localhost:8089";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HashMap<String, String> body = new HashMap<>();
        body.put("transaction", transaction);
        body.put("phone_number", phoneNumber);
        body.put("amount", String.valueOf(amount));

        Gson gson = new Gson();
        HttpEntity<String> request = new HttpEntity<>(gson.toJson(body), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://" + domain + "/voucher",
                    request,
                    String.class
            );

            // for demo of timeout case only
            Random rand = new Random();
            int next = rand.nextInt(50);
            if (next % 2 == 0) {
                throw new TimeoutException();
            }

            if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                throw new VoucherProcessingException();
            }

            GenerateVoucherResp resp = gson.fromJson(response.getBody(), GenerateVoucherResp.class);
            return resp.getVoucherCode();
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() != HttpStatus.CONFLICT) {
                throw e;
            }
            GenerateVoucherResp resp = gson.fromJson(e.getResponseBodyAsString(), GenerateVoucherResp.class);
            assert resp != null;
            switch (resp.getCode()) {
                case GenerateVoucherResp.RESPONSE_SUCCESS:
                    return resp.getVoucherCode();
                case GenerateVoucherResp.RESPONSE_FAILED:
                    throw new VoucherCreationException();
                case GenerateVoucherResp.RESPONSE_PROCESSING:
                    throw new VoucherProcessingException();
                default:
                    throw new IllegalStateException("Unexpected value: " + resp.getCode());
            }
        } catch (VoucherProcessingException e) {
            // http 202 in above code
            throw e;
        } catch (Exception e) {
            HashMap<String, Object> message = new HashMap<>();
            message.put("transaction", transaction);
            message.put("phone_number", phoneNumber);
            message.put("amount", amount);
            messageQueue.sendMessage(DELAYED_PURCHASE_QUEUE, gson.toJson(message));
            throw new VoucherProcessingException();
        }

    }

    @Override
    public List<Voucher> getVoucherByPhoneNumber(String phoneNumber) {
        String domain = System.getenv("VOUCHER_SERVICE_URL");
        if (domain == null || domain.equals("")) {
            domain = "localhost:8089";
        }

        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://" + domain + "/voucher/list?phone_number=" + phoneNumber,
                String.class
        );

        Gson gson = new Gson();
        try {
            Type type = new TypeToken<List<Voucher>>() {
            }.getType();

            return gson.fromJson(response.getBody(), type);
        } catch (HttpStatusCodeException e) {
            return new ArrayList<>();
        }
    }
}
