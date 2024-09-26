package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepositoryImpl pointRepository;

    @Test
    public void 유저의_포인트를_조회() {

        //given
        long id = 1L;
        long amount = 100L;
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());

        //when
        when(pointRepository.selectById(id)).thenReturn(userPoint);

        UserPoint getUserPoint = pointService.getUserPoint(id);

        //then
        assertEquals(getUserPoint.id(), userPoint.id());
        assertEquals(getUserPoint.point(), userPoint.point());
        assertEquals(getUserPoint.updateMillis(), userPoint.updateMillis());

        verify(pointRepository).selectById(id);

    }

    @Test
    public void 존재하지_않는_유저의_포인트_조회() {

        //given
        long id = -1L;

        //when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            pointService.getUserPoint(id);
        });

        //then
        assertEquals(e.getMessage(), "id가 없습니다.");


    }

    @Test
    public void 유저의_포인트_내역_조회() {

        //given
        long userId = 1L;
        long id1 = 1L;
        long id2 = 2L;

        //when
        List<PointHistory> pointHistory = List.of(
                new PointHistory(id1, userId, 1000, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(id2, userId, 500, TransactionType.USE, System.currentTimeMillis())
        );

        when(pointRepository.selectHistory(userId)).thenReturn(pointHistory);

        List<PointHistory> thenPointHistory = pointService.history(userId);

        //then
        assertEquals(thenPointHistory.size(), pointHistory.size());

        for (int i = 0; i < pointHistory.size(); i++) {
            assertEquals(thenPointHistory.get(i).id(), pointHistory.get(i).id());
            assertEquals(thenPointHistory.get(i).userId(), pointHistory.get(i).userId());
            assertEquals(thenPointHistory.get(i).amount(), pointHistory.get(i).amount());
            assertEquals(thenPointHistory.get(i).type(), pointHistory.get(i).type());
        }

        verify(pointRepository).selectHistory(userId);
    }

    @Test
    public void 유저의_포인트_충전() {

        //given
        long id = 1L;
        long pointBefore = 1000L;
        long chargePoint = 400L;
        long totalPoint = pointBefore - chargePoint;

        //when
        UserPoint userPointAfterUse = new UserPoint(id, totalPoint, System.currentTimeMillis());
        when(pointRepository.updatePoint(id, totalPoint)).thenReturn(userPointAfterUse);

        UserPoint thenUserPoint = pointService.usePoint(id, chargePoint);

        //then
        assertEquals(thenUserPoint.id(), id);
        assertEquals(thenUserPoint.point(), totalPoint);

        verify(pointRepository).updatePoint(id, totalPoint);

    }

    @Test
    public void 유저의_포인트_사용() {

        //given
        long id = 1L;
        long pointBefore = 1000L;
        long usePoint = 400L;
        long totalPoint = pointBefore - usePoint;

        //when
        UserPoint userPointAfterUse = new UserPoint(id, totalPoint, System.currentTimeMillis());
        when(pointRepository.updatePoint(id, totalPoint)).thenReturn(userPointAfterUse);

        UserPoint thenUserPoint = pointService.usePoint(id, usePoint);

        //then
        assertEquals(thenUserPoint.id(), id);
        assertEquals(thenUserPoint.point(), totalPoint);

        verify(pointRepository).updatePoint(id, totalPoint);
    }
}
