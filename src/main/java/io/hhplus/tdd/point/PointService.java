package io.hhplus.tdd.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    // 포인트 조회
    public UserPoint getUserPoint(long id) {
        return pointRepository.findById(id);
    }

    // 포인트 히스토리 조회
    public List<PointHistory> getUserPointHistory(long id) {
        return pointHistoryRepository.findAllByUserId(id);
    }

    // 포인트 충전
    public UserPoint charge(long id, long amount) {
        // 유저 포인트 조회를 UserPoint 객체로 위임
        UserPoint userPoint = pointRepository.findById(id);

        // 포인트 충전
        userPoint = userPoint.addPoints(amount); //로직을 dto에서 작성하고 서비스에서 합쳐줌 실제로직은 x

        // 포인트 충전
        pointRepository.save(userPoint);

        // 포인트 충전 히스토리 작성
        PointHistory pointHistory = PointHistory.create(id, amount, TransactionType.CHARGE);

        // 포인트 저장
        pointHistoryRepository.save(pointHistory.userId(), pointHistory.amount(), pointHistory.type(), pointHistory.updateMillis());

        return userPoint;
    }

    public UserPoint use(long id, long amount) {
        UserPoint userPoint = pointRepository.findById(id);

        // 포인트 사용
        userPoint = userPoint.usePoints(amount);
        pointRepository.save(userPoint);

        // 포인트 사용 히스토리 작성
        PointHistory pointHistory = PointHistory.create(id, amount, TransactionType.USE);

        // 포인트 저장
        pointHistoryRepository.save(pointHistory.userId(), pointHistory.amount(), pointHistory.type(), pointHistory.updateMillis());

        return userPoint;
    }
}
