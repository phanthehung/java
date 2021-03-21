package com.example.voucher.boundedcontext.voucher.domain.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
public class Voucher {


    public enum Status {
        processing {
            @Override
            Status[] getNextStatuses() {
                return new Status[]{Status.success, Status.failed};
            }
        },
        failed {
            @Override
            Status[] getNextStatuses() {
                return new Status[]{};
            }
        },
        success {
            @Override
            Status[] getNextStatuses() {
                return new Status[]{};
            }
        };

        abstract Status[] getNextStatuses();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voucher", length = 10)
    private Integer id;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "transaction", unique = true, length = 100)
    private String transaction;

    @Column(name = "amount")
    private double amount;

    @Column(name = "retry")
    private int retry;

    @Column(name = "voucher_code", unique = true, length = 20)
    private String voucherCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status", length = 16)
    @Enumerated(EnumType.STRING)
    private Status status;

    public Voucher() {
    }

    public Voucher(String phoneNumber, String transaction, double amount, int retry, String voucherCode, LocalDateTime createdAt, Status status) {
        this.phoneNumber = phoneNumber;
        this.transaction = transaction;
        this.amount = amount;
        this.retry = retry;
        this.voucherCode = voucherCode;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTransaction() {
        return transaction;
    }

    public double getAmount() {
        return amount;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getRetry() {
        return retry;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isValidTransition(Status current, Status next) {
        Status[] possibleStatuses = current.getNextStatuses();
        for (Status possibleStatus : possibleStatuses) {
            if (possibleStatus.name().equals(next.name())) {
                return true;
            }
        }
        return false;
    }
}