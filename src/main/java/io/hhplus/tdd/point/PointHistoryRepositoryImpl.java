package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository{

    private final PointHistoryTable pointHistoryTable;

    @Override
    public List<PointHistory> findAllByUserId(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    @Override
    public PointHistory save(long userId, long amount, TransactionType type, long updateMillis) {
        return pointHistoryTable.insert(userId, amount, type, updateMillis);
    }
}
