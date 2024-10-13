package io.hhplus.tdd.point.unit;

import io.hhplus.tdd.point.*;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 확장을 통해 Mockito가 테스트에서 사용할 목업 주입을 설정
public class ChargeUserPointUnitTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    void 포인트충전_성공케이스() {
        // given 초기화 단계
        long userId = 1L;
        long currentAmount = 2000L;
        long chargeAmount = 3000L;
        long upMills = 1728309524817L;

        UserPoint userPoint = new UserPoint(userId, currentAmount, upMills); // db에 저장되어 있다고 가정
        UserPoint updateUserPoint = new UserPoint(userId, currentAmount + chargeAmount, upMills);

        // when 실행 단계
        when(pointRepository.findById(eq(userId))).thenReturn(userPoint); // mock을 통해 기존 포인트 정보(만들어 놓은 객체) 반환
        when(pointRepository.save(any(UserPoint.class))).thenReturn(updateUserPoint);

        // then 검증 단계
        UserPoint result = pointService.charge(userId, chargeAmount);

        // 충전 후 결과 검증
        assertNotNull(result); // null 검증
        assertEquals(currentAmount + chargeAmount, result.point()); // 포인트 합계 검증

        // findById와 save가 올바르게 호출되었는지 검증
        verify(pointRepository, times(1)).findById(eq(userId));
//        verify(pointRepository).save(eq(updateUserPoint));

        // save 히스토리 메서드가 한번이라도 해당 객체들로 호출 되었었나 검증
        verify(pointHistoryRepository).save(eq(userId), eq(chargeAmount), eq(TransactionType.CHARGE), anyLong());
    }

    @Test
    void 충전금액이_0_이하일때_예외케이스() {
        // given
        long userId = 1L;
        long currentPoint = 1000L;
        long chargeAmount = 0L; // 유효하지 않은 금액 (0이하)

        UserPoint userPoint = new UserPoint(userId, currentPoint, System.currentTimeMillis());

        // when
        when(pointRepository.findById(eq(userId))).thenReturn(userPoint);

        // then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> pointService.charge(userId, chargeAmount));
        assertEquals("충전 포인트는 0보다 커야합니다.", exception.getMessage());

        // 충전 금액이 0 이하일 땐 save는 호출되지 않아야 함
        verify(pointRepository, never()).save(any());
        verify(pointHistoryRepository, never()).save(userId, 0L, TransactionType.CHARGE, userPoint.updateMillis());
    }

    @Test
    void 충전금액_최대한도를_넘었을때_예외케이스() {
        // given
        long userId = 1L;
        long currentPoint = 1000L;
        long chargeAmount = 100000L; // 유효하지 않은 금액

        UserPoint userPoint = new UserPoint(userId, currentPoint, System.currentTimeMillis());

        // when
        when(pointRepository.findById(eq(userId))).thenReturn(userPoint);

        // then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> pointService.charge(userId, chargeAmount));
        assertEquals("충전 금액의 최대한도는 99999입니다.", exception.getMessage());

        // 충전 금액이 0 이하일 땐 save는 호출되지 않아야 함
        verify(pointRepository, never()).save(any());
        verify(pointHistoryRepository, never()).save(userId, 0L, TransactionType.CHARGE, userPoint.updateMillis());
    }

    @Test
    void 포인트합계가_음수일때_예외케이스() {
        // given
        long userId = 1L;
        long currentAmount = -2L;
        long chargeAmount = 1L;  // 포인트 합계가 음수가 될 수 있는 금액

        UserPoint userPoint = new UserPoint(userId, currentAmount, System.currentTimeMillis());

        // when
        when(pointRepository.findById(eq(userId))).thenReturn(userPoint);  // 유저 포인트 정보를 반환

        // then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.charge(userId, chargeAmount);
        });

        // 음수가 되기 전에
        assertEquals("포인트 합계가 잘못되었습니다. 비정상적인 금액을 충전하려고 합니다.", exception.getMessage());

        // 포인트 합계가 음수일 때 saveOrUpdate는 호출되지 않아야 함
        verify(pointRepository, never()).save(userPoint);
        verify(pointHistoryRepository, never()).save(anyLong(), anyLong(), any(), anyLong());
    }

    @Test
    void 포인트사용_성공케이스() {
        // given
        long userId = 1L;
        long currentAmount = 2000L;
        long useAmount = 500L;
        long upMills = 1728309524817L;

        UserPoint userPoint = new UserPoint(userId, currentAmount, upMills);
        UserPoint updateUserPoint = new UserPoint(userId, currentAmount - useAmount, upMills);

        // when
        when(pointRepository.findById(eq(userId))).thenReturn(userPoint);
        when(pointRepository.save(any(UserPoint.class))).thenReturn(updateUserPoint);

        // then
        UserPoint result = pointService.use(userId, useAmount);
        assertNotNull(result);
        assertEquals(currentAmount - useAmount, result.point());

        verify(pointRepository, times(1)).findById(eq(userId));
        verify(pointRepository).save(any(UserPoint.class));

        verify(pointHistoryRepository).save(eq(userId), eq(useAmount), eq(TransactionType.USE), anyLong());
    }
}
