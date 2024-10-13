package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.TransactionType;

import java.util.List;

public interface PointHistoryRepository {

    List<PointHistory> findAllByUserId(long id);

    PointHistory save(long userId, long amount, TransactionType type, long updateMillis);
}
