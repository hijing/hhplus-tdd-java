package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.TransactionType;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
    public static PointHistory create(long id, long amount, TransactionType transactionType) {
        return new PointHistory(
                System.currentTimeMillis(),
                id,
                amount,
                transactionType,
                System.currentTimeMillis()
        );
    }
}
