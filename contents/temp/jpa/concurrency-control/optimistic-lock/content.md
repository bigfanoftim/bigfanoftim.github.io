# 낙관적 락(Optimistic Lock)에 재시도 전략(RetryStrategy)을 적용하여 동시성 제어하기

### 읽기 전에 미리 알면 좋은 내용들
- [MySQL 엔진의 잠금](../../../database/mysql/mysql-engine-lock/content.md)
- [InnoDB 스토리지 엔진 잠금](../../../database/mysql/innodb-storage-engine-lock/content.md)
- [InnoDB 인덱스와 잠금](../../../database/mysql/innodb-index-lock/content.md)
- [트랜잭션 격리 수준](../../../database/mysql/transaction-isolation-level/content.md)
- [비관적 락(Pessimistic Lock)을 적용하여 동시성 제어하기](../../../jpa/concurrency-control/pessimistic-lock/content.md) 

---

낙관적 락(Optimistic Lock)은 비관적 락과 마찬가지로 동시성 제어를 위한 전략 중 하나이다. 여러 트랜잭션이 동시에 레코드에 접근할 때 충돌할 가능성을 확인하는 방식이다.

이러한 충돌 감지는 트랜잭션 시작 시점에 조회된 데이터의 버전과 커밋 시점에 데이터의 버전을 비교하는 방식으로 이루어진다. 데이터베이스의 버전을 저장하고 비교하고를 반복하며 충돌을 감지하기 때문에 해당 레코드에 잠금을 걸 필요가 없다.

따라서 낙관적 락은 실제로 잠금을 거는 것은 아니기 때문에 비관적 락에 비해 성능상 이점이 있을 수 있다. 또한 데드락 현상을 피할 수 있어 시스템의 안정성을 높인다고 볼 수 있다. 

하지만 동시에 많은 트랜잭션에서 하나의 레코드에 대한 수정 작업이 발생하면 잦은 충돌이 발생할 것이고 이로 인한 성능 저하가 있을 수 있다. 게다가 충돌을 감지하고 충돌 시 어떻게 처리해야 할지 로직으로 구현해야 하기 때문에 설계와 구현이 보다 복잡해질 것이다.

그러므로 잦은 충돌이 예상되는 경우 낙관적 락을 피하고 다른 방식을 고려하는 것이 좋을 수 있다.

### JPA에서 낙관적 락을 다루는 방법
> 엔티티의 자세한 정보는 [비관적 락(Pessimistic Lock)을 적용하여 동시성 제어하기](../../../jpa/concurrency-control/pessimistic-lock/content.md) 참고

앞서 설명한 것처럼 낙관적 락은 데이터에 특별한 잠금을 거는 것이 아니라 트랜잭션에서 버전을 비교해가는 방식이다. JPA에서 지원하는 `@Version` 어노테이션을 사용하여 엔티티의 버전을 관리할 수 있다.


```java
@Entity
public class Product {

    // ...
    
    @Version
    private Long version;
    
    // ...
}
```

비관적 락과 마찬가지로 레포지토리에 `@Lock` 어노테이션을 사용하여 메소드를 추가해주자. 

```java
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdWithOptimisticLock(@Param("id") Long id);
}
```

그리고 추가한 메소드를 활용하는 서비스 레이어를 아래와 같이 구현하자.

```java
@RequiredArgsConstructor
@Service
public class OptimisticLockProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void purchase(Long productId, Long quantity) {
        Product product = productRepository.findByIdWithOptimisticLock(productId).orElseThrow();
        product.purchase(quantity);
    }
}
```

위에서 설명한 것처럼 낙관적 락은 버전을 비교해가면서 충돌을 감지해야 하기 때문에 그에 맞는 로직(충돌 시 재시도)을 직접 구현해야 한다. 

이러한 재시도 전략의 경우 전체 시스템에 걸쳐서 사용되는 cross-cutting concern이라고 볼 수 있다.

따라서 적절한 역할 분리를 위해 별개의 `Strategy` 인터페이스와 구현체를 생성하고 이와 조합해 `OptimisticLockProductService`를 사용하는 `OptimisticLockProductFacade` 클래스를 구현할 것이다. 

```java
public interface RetryStrategy {

    void retry(Runnable action) throws Exception;
}
```

```java
@Primary
@Component
public class ExponentialBackoffRetryStrategy implements RetryStrategy {

    private final static int MAX_RETRIES = 30;
    private final static long INITIAL_SLEEP_TIME = 50; // millisecond

    @Override
    public void retry(Runnable action) {
        int retries = 0;
        long sleepTime = INITIAL_SLEEP_TIME;

        while (retries < MAX_RETRIES) {
            try {
                action.run();
                break;
            } catch (Exception e) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                retries++;
                sleepTime *= 2;
            }
        }

        // MaxRetriesExceededException
        throw new RuntimeException("Maximum retries exceeded");
    }
}
```

```java
@RequiredArgsConstructor
@Component
public class OptimisticLockProductFacade {

    private final OptimisticLockProductService optimisticLockProductService;
    private final RetryStrategy retryStrategy;


    public void purchase(Long productId, Long quantity) throws Exception {
        retryStrategy.retry(() -> optimisticLockProductService.purchase(productId, quantity));
    }
}
```

재고 차감 비즈니스 로직과 공통 관심사에 대한 적절한 역할 분리를 진행하여 클라이언트에는 간단한 인터페이스만 제공하여 변경에 용이한 설계라고 볼 수 있다.

그러나 위에서 구현한 재시도 전략의 경우 `MAX_RETRIES`, `INITIAL_SLEEP_TIME` 및 세부 로직은 현재 개발하고 있는 비즈니스에 따라 디테일하게 개선할 필요가 있다. (Exponential Backoff가 아닌 다른 전략을 구현하는 것도 좋은 방법)

테스트 코드는 [비관적 락(Pessimistic Lock)을 적용하여 동시성 제어하기](../../../jpa/concurrency-control/pessimistic-lock/content.md)에서 다룬 것과 동일하게 가져가면 된다. 