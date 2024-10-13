package io.hhplus.tdd.point.integration;

import io.hhplus.tdd.TddApplication;
import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TddApplication.class) // Spring 컨텍스트를 로드하여 통합 테스트 수행
public class ChargeUserPointIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Test
    void 포인트충전_성공케이스() {
        // given
        long userId = 1L;
        long currentAmount = 1000L;
        long chargeAmount = 4000L;
        long upMills = 1728309524817L;

        UserPoint userPoint = new UserPoint(userId, currentAmount, upMills);
        // 미리 유저 포인트를 저장
        pointRepository.save(userPoint);

        // when
        UserPoint updateUser = pointService.charge(userId, chargeAmount);
        assertNotNull(updateUser);
        assertEquals(currentAmount + chargeAmount, updateUser.point());

        // then 실제 DB에 저장된 포인트를 확인
        UserPoint result = pointRepository.findById(userId);
        assertNotNull(result);
        assertEquals(currentAmount + chargeAmount, result.point());

        // 포인트 히스토리 저장도 확인
        List<PointHistory> pointHistoryList = pointHistoryRepository.findAllByUserId(result.id());
        assertEquals(1, pointHistoryList.size());
        assertEquals(chargeAmount, pointHistoryList.get(0).amount());
        assertEquals(TransactionType.CHARGE, pointHistoryList.get(0).type());
    }

    @Test
    void 포인트사용_성공케이스() {
        // given
        long userId = 1L;
        long currentAmount = 2000L;
        long useAmount = 500L;
        long upMiles = 1728309524817L;

        UserPoint userPoint = new UserPoint(userId, currentAmount, upMiles);
        // 유저 포인트를 저장
        pointRepository.save(userPoint);

        // when
        UserPoint updateUser = pointService.use(userId, useAmount);
        assertNotNull(updateUser);
        assertEquals(currentAmount - useAmount, updateUser.point());

        // then
        UserPoint result = pointRepository.findById(userId);
        assertNotNull(result);
        assertEquals(currentAmount - useAmount, result.point());

        // 포인트 히스토리 저장도 확인
        List<PointHistory> pointHistoryList = pointHistoryRepository.findAllByUserId(result.id());
        assertEquals(1, pointHistoryList.size());
        assertEquals(useAmount, pointHistoryList.get(0).amount());
        assertEquals(TransactionType.USE, pointHistoryList.get(0).type());
    }
}

