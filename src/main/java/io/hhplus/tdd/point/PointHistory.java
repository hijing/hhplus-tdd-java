package io.hhplus.tdd.point;

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
