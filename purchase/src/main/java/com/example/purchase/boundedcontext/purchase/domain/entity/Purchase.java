package com.example.purchase.boundedcontext.purchase.domain.entity;

import com.example.purchase.boundedcontext.purchase.exception.InvalidStateTransitionException;

import javax.persistence.*;

@Entity
@Table(name = "purchase")
public class Purchase {

    public enum Status {
        pending {
            @Override
            Status[] getNextStatuses() {
                return new Status[] {Status.confirmed, Status.cancel};
            }
        },
        confirmed {
            @Override
            Status[] getNextStatuses() {
                return new Status[] {Status.processing, Status.success, Status.failed};
            }
        },
        processing {
            @Override
            Status[] getNextStatuses() {
                return new Status[] {Status.success, Status.failed};
            }
        },
        cancel {
            @Override
            Status[] getNextStatuses() {
                return new Status[] {};
            }
        },
        failed {
            @Override
            Status[] getNextStatuses() {
                return new Status[] {};
            }
        },
        success {
            @Override
            Status[] getNextStatuses() {
                return new Status[] {};
            }
        };

        abstract Status[] getNextStatuses();
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

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id_purchase", length = 10)
    private Integer id;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "transaction", unique = true, length = 100)
    private String transaction;

    @Column(name = "credit_card_number", length = 16)
    private String creditCardNumber;

    @Column(name = "amount")
    private double amount;

    @Column(name = "status", length = 16)
    @Enumerated(EnumType.STRING)
    private Status status;

    public Integer getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public double getAmount() {
        return amount;
    }

    public Status getStatus() {
        return status;
    }

    public Purchase() {
    }

    public Purchase(String phoneNumber, String transaction, String creditCardNumber, double amount, Status status) {
        this.phoneNumber = phoneNumber;
        this.transaction = transaction;
        this.creditCardNumber = creditCardNumber;
        this.amount = amount;
        this.status = status;
    }

    public void transitToNextStatus(Status newStatus) throws InvalidStateTransitionException {
        if (!this.isValidTransition(this.getStatus(), newStatus)) {
            throw new InvalidStateTransitionException(this.getStatus(), newStatus, this.getTransaction());
        }
        this.status = newStatus;
    }
}
