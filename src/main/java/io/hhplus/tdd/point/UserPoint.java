package io.hhplus.tdd.point;

import org.apache.catalina.User;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint addPoints(long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("충전 포인트는 0보다 커야합니다.");
        } else if (amount > 99999) {
            throw new IllegalArgumentException("충전 금액의 최대한도는 99999입니다.");
        }

        long newPoint = this.point + amount;

        if(newPoint < 0) {
            throw new IllegalArgumentException("포인트 합계가 잘못되었습니다. 비정상적인 금액을 충전하려고 합니다.");
        }
        
        // 새로운 유저 포인트 리턴
        return new UserPoint(this.id, newPoint, System.currentTimeMillis());
    }

    public UserPoint usePoints(long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("사용 포인트는 0보다 커야합니다.");
        } else if (amount > point) {
            throw new IllegalArgumentException("충전포인트는 사용보인트보다 클 수 없습니다.");
        }
        long newPoint = this.point - amount;

        return new UserPoint(this.id, newPoint, System.currentTimeMillis());
    }
}
