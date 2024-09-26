package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointRepositoryImpl implements PointRepository{

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    @Autowired
    public PointRepositoryImpl(PointHistoryTable pointHistoryTable, UserPointTable userPointTable) {
        this.pointHistoryTable = pointHistoryTable;
        this.userPointTable = userPointTable;
    }

    @Override
    public UserPoint selectById(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public List<PointHistory> selectHistory(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    @Override
    public UserPoint updatePoint(long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }

    @Override
    public void updateHistory(long id, long amount, TransactionType transactionType, long updateMiles) {
        pointHistoryTable.insert(id, amount, transactionType, updateMiles);
    }
}
