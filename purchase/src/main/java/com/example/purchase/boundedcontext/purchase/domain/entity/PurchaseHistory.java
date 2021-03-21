package com.example.purchase.boundedcontext.purchase.domain.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_history")
public class PurchaseHistory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id_purchase_history", length = 10)
    private Integer id;

    @Column(name = "transaction", length = 10)
    private String transaction;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "time")
    private LocalDateTime time;

    public Integer getId() {
        return id;
    }

    public PurchaseHistory() {
    }

    public PurchaseHistory(String transaction, String status, LocalDateTime time) {
        this.transaction = transaction;
        this.status = status;
        this.time = time;
    }

    public PurchaseHistory(Integer id, String transaction, String status, LocalDateTime time) {
        this.id = id;
        this.transaction = transaction;
        this.status = status;
        this.time = time;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
