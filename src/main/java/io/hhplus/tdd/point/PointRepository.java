package io.hhplus.tdd.point;

import java.util.List;

public interface PointRepository {

    UserPoint selectById(long id);
    List<PointHistory> selectHistory(long id);
    UserPoint updatePoint(long id, long amount);
    void updateHistory(long id, long amount, TransactionType transactionType, long updateMiles);
}
