package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.domain.UserPoint;

public interface PointRepository {

    UserPoint findById(long id);

    UserPoint save(UserPoint userPoint);
}
