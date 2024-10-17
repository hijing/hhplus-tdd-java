package io.hhplus.tdd.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private final PointRepository pointRepository;

    @Autowired
    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }
    
    //포인트 조회
    public UserPoint getUserPoint(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id가 없습니다.");
        }
        return pointRepository.selectById(id);
    }
    
    //포인트 이용내역 조회
    public List<PointHistory> history(long id) {
        return pointRepository.selectHistory(id);
    }
    
    //포인트 충전
    public UserPoint chargePoint(long id, long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("충전 금액이 0보다 작습니다.");
        }
        //유저포인트 조회
        UserPoint userPoint = pointRepository.selectById(id);
        long totalPoint = userPoint.point() + amount;
        
        //사용여부 가능 조회
        if (totalPoint < 0) {
            throw new IllegalArgumentException("포인트 충전 금액이 잘못되었습니다.");
        }
        //충전 히스토리 저장
        pointRepository.updateHistory(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return pointRepository.updatePoint(userPoint.id(), totalPoint);
    }
    
    //포인트 사용
    public UserPoint usePoint(long id, long amount) {
        //유저포인트 조회
        UserPoint userPoint = pointRepository.selectById(id);
        long totalPoint = userPoint.point() - amount;
        if (totalPoint < 0) {
            throw new IllegalArgumentException("보유 포인트보다 사용 포인트가 큽니다.");
        }
        pointRepository.updateHistory(id, amount, TransactionType.USE, System.currentTimeMillis());
        return pointRepository.updatePoint(userPoint.id(), totalPoint);
    }

}
