package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository{

    private final UserPointTable userPointTable;

    @Override
    public UserPoint findById(long id) {
        UserPoint userPoint = userPointTable.selectById(id);

        if(userPoint == null) {
            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
        }
        return userPoint;
    }

    @Override
    public UserPoint save(UserPoint userPoint) {
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
    }

}
