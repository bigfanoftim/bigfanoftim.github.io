# @Transactional 어노테이션과 synchronized 함께 사용

```Java
@Transactional
public synchronized void decrease(Long id, Long quantity) {
    Stock stock = repository.findById(id).orElseThrow();
    stock.decrease(quantity);
    stockRepository.saveAndFlush(stock);
}
```

위와 같이 재고를 원하는 양(quantity)만큼 감소시키는 메소드가 있다. synchronized 키워드를 더해 `decrease` 메소드에 오직 하나의 스레드만 접근할 수 있도록 한다.

하지만 테스트를 해보면 의도한대로 동작하지 않는다.

### @Transactional 어노테이션 동작
- `@Transactional` 어노테이션은 프록시 기반의 AOP를 사용함
- 따라서 해당 어노테이션이 붙은 클래스 혹은 메소드가 실행되면 Spring은 그 클래스 혹은 메소드를 바로 실행하는 대신 프록시 객체를 생성하고 실행함
- 프록시는 메소드 호출 전 트랜잭션을 시작하고, 메소드 호출 후에 커밋 or 롤백을 진행

### synchronized 키워드와 함께 사용되면
- 프록시 객체가 생성되고 그 객체에 `synchronized` 키워드가 적용됨
- 실제 비즈니스 로직을 수행할 `decrease` 메소드가 아닌 프록시 객체에 동기화가 적용됨

### 해결책
- `@Transactional`이 매핑된 메소드와는 별도의 객체로 분리하여 그 객체에 `synchronized` 키워드 사용