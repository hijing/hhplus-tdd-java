package io.hhplus.tdd.point;

import java.util.List;

public interface PointHistoryRepository {

    List<PointHistory> findAllByUserId(long id);

    PointHistory save(long userId, long amount, TransactionType type, long updateMillis);
}
