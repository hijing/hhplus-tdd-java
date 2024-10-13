# hhplus-tdd-java

### ❓ [과제] `point` 패키지의 TODO 와 테스트코드를 작성해주세요.

**요구 사항**

- PATCH  `/point/{id}/charge` : 포인트를 충전한다.
- PATCH `/point/{id}/use` : 포인트를 사용한다.
- GET `/point/{id}` : 포인트를 조회한다.
- GET `/point/{id}/histories` : 포인트 내역을 조회한다.
- 잔고가 부족할 경우, 포인트 사용은 실패하여야 합니다.
- 동시에 여러 건의 포인트 충전, 이용 요청이 들어올 경우 순차적으로 처리되어야 합니다.
  
### `Default`

- `/point` 패키지 (디렉토리) 내에 `PointService` 기본 기능 작성
- `/database` 패키지의 구현체는 수정하지 않고, 이를 활용해 기능을 구현
- 각 기능에 대한 단위 테스트 작성

> 총 4가지 기본 기능 (포인트 조회, 포인트 충전/사용 내역 조회, 충전, 사용) 을 구현합니다.

### `Step 1`

- 포인트 충전, 사용에 대한 정책 추가 (잔고 부족, 최대 잔고 등)
    - 동시에 여러 요청이 들어오더라도 순서대로 (혹은 한번에 하나의 요청씩만) 제어될 수 있도록 리팩토링
- 동시성 제어에 대한 통합 테스트 작성
    
### `Step 2`

- 동시성 제어 방식에 대한 분석 및 보고서 작성 ( **README.md** )


## 동시성 문제 해결을 위한 추가적인 조치

캐싱은 데이터베이스 접근을 줄이고 성능을 향상시키는 중요한 기법이다. 
하지만 동시에 비동기적으로 많은 요청이 들어올 때, 캐시에서 값을 차감하는 로직이 순차적으로 잘 처리되지 않으면 잔여 크레딧 이슈가 발생할 수 있어 이를 해결하기 위해 동기화 블록을 활용하는 방법이 있다.


### 1. 동기화 블록 (Synchronization)
자바의 synchronized 키워드를 사용하여 특정 블록을 동기화하면 한 번에 하나의 스레드만 접근할 수 있도록 할 수 있다.

```
public boolean deductUsageFromCache(String workUrl, Long memberIdx, int deductAmount) {
    synchronized (this) {
        // 캐시 로직
    }
}
```

### 2. ReentrantLock 사용
ReentrantLock을 사용하면 더 정교한 잠금 메커니즘을 제공하여 동시성 문제를 해결할 수 있다.

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GenerativeAIService {
    private final Lock lock = new ReentrantLock();

    public boolean deductUsageFromCache(String workUrl, Long memberIdx, int deductAmount) {
        lock.lock();
        try {
            // 캐시 로직
        } finally {
            lock.unlock();
        }
    }
}

### 3. 사용자별 잠금
동일 사용자가 여러 번 호출할 때의 동시성 문제를 해결하기 위해 사용자별로 잠금을 설정할 수 있다. 
이를 위해 ConcurrentHashMap을 사용하여 각 사용자에 대해 ReentrantLock을 관리한다.

```
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GenerativeAIService {
    private final ConcurrentHashMap<String, Lock> userLocks = new ConcurrentHashMap<>();

    private Lock getUserLock(String userKey) {
        return userLocks.computeIfAbsent(userKey, k -> new ReentrantLock());
    }

    public boolean deductUsageFromCache(String workUrl, Long memberIdx, int deductAmount) {
        String userKey = workUrl + ":" + memberIdx; // 사용자별 키 생성
        Lock lock = getUserLock(userKey);

        lock.lock();
        try {
            // 캐시 로직
        } finally {
            lock.unlock();
        }
    }
}
```

### ReentrantLock의 성능 이점
더 정교한 잠금 메커니즘: ReentrantLock은 synchronized보다 더 많은 기능을 제공한다. 예를 들어, 시도 가능한 잠금과 타임아웃 기능이 있다.
공정성 옵션: 공정성을 보장하여 스레드가 들어온 순서대로 잠금을 획득하게 할 수 있다.
조건 객체: 조건 객체를 사용하여 더 정교한 스레드 통신을 구현할 수 있다.

### 결론
ReentrantLock을 사용하면 synchronized보다 더 정교한 제어가 가능하며, 사용자별로 잠금을 관리하여 동일 사용자의 여러 호출을 순차적으로 처리할 수 있다.
이를 통해 캐싱과 동시성 문제를 효과적으로 해결하고, 성능을 최적화할 수 있다.
