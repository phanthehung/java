package com.example.voucher.boundedcontext.voucher.infrastructure;

import com.example.voucher.boundedcontext.voucher.domain.VoucherRepositoryInterface;
import com.example.voucher.boundedcontext.voucher.domain.entity.Voucher;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

public class VoucherRepository implements VoucherRepositoryInterface {

    private EntityManager entityManager;

    public VoucherRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void saveVoucher(Voucher voucher) {
        entityManager.persist(voucher);
    }

    @Override
    @Transactional
    public void updateVoucherCodeOnSuccess(String transaction, String voucherCode) {
        entityManager.createQuery(
                "UPDATE Voucher v set v.voucherCode = :voucherCode, v.status = :status  WHERE v.transaction = :transaction"
        )
                .setParameter("voucherCode", voucherCode)
                .setParameter("status", Voucher.Status.success)
                .setParameter("transaction", transaction)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateRetryCountByTransaction(String transaction) {
        entityManager.createQuery(
                "UPDATE Voucher v set v.retry = v.retry + 1 WHERE v.transaction = :transaction"
        ).setParameter("transaction", transaction).executeUpdate();
    }

    @Override
    @Transactional
    public void setVoucherToFailed(String transaction) {
        entityManager.createQuery(
                "UPDATE Voucher v set v.status = :status WHERE v.transaction = :transaction"
        )
                .setParameter("status", Voucher.Status.failed)
                .setParameter("transaction", transaction)
                .executeUpdate();
    }

    @Override
    @Transactional
    public Voucher getVoucherByTransaction(String transaction) {
        try {
            return entityManager.createQuery(
                    "SELECT v from Voucher v WHERE v.transaction = :transaction", Voucher.class).
                    setParameter("transaction", transaction).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    @Transactional
    public List<Voucher> getVouchersByPhoneNumber(String phoneNumber) {
        return entityManager.createQuery(
                "SELECT v from Voucher v WHERE v.phoneNumber = :phoneNumber", Voucher.class).
                setParameter("phoneNumber", phoneNumber).getResultList();
    }
}
