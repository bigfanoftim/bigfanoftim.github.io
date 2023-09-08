## Database

### MySQL

- [MySQL 엔진의 잠금](database/mysql/mysql-engine-lock/content.md)
- [InnoDB 스토리지 엔진 잠금](database/mysql/innodb-storage-engine-lock/content.md)
- [InnoDB 인덱스와 잠금](database/mysql/innodb-index-lock/content.md)
- [트랜잭션 격리 수준](database/mysql/transaction-isolation-level/content.md)

## Java

- [JDK, JRE, JVM](java/jdk-jre-jvm/content.md) 
- [JVM의 구조](java/jvm-architecture/content.md)
- [Reflection이란?](java/reflection/content.md)
- [람다식(Lambda Expression)](java/lambda-expression/README.md)
- [스트림(stream)](stream/src/main/java/README.md)

### 이펙티브 자바

- 아이템 1. 생성자 대신 정적 팩토리 메소드를 고려하라
    - [생성자 대신 정적 팩토리 메소드를 사용하는 것의 5가지 장점](effective-java/src/main/java/chapter01/item01/content.md)
    - [생성자 대신 정적 팩토리 메소드를 사용하는 것의 2가지 단점](effective-java/src/main/java/chapter01/item01/cons.md)

## Spring

- [@Transactional 어노테이션과 synchronized 함께 사용](spring/transactional-annotation-with-synchronized/content.md)
- [@Autowired 어노테이션을 사용하면서 필드 및 Setter로 의존성 주입 시 발생할 수 있는 문제](spring/autowired-issues/content.md)

### Spring Security

- [WebSecurityConfigurerAdapter가 아닌 component-based security configuration 방식으로 Spring Security Filter 구현](spring/spring-security/component-based-security-configuration/content.md)
- [커스텀 HandlerMethodArgumentResolver 구현](spring/spring-security/handler-method-argument-resolver/content.md)

## JPA

### 동시성 제어
- [비관적 락(Pessimistic Lock)을 적용하여 동시성 제어하기](jpa/concurrency-control/pessimistic-lock/content.md) 
- [낙관적 락(Optimistic Lock)에 재시도 전략(RetryStrategy)을 적용하여 동시성 제어하기](jpa/concurrency-control/optimistic-lock/content.md)
- [네임드 락(Named Lock)의 동시성 제어와 주의할 점](jpa/concurrency-control/named-lock/content.md)

### Spring Data JPA

- [Spring Data JPA의 다양한 예시](jpa/spring-data-jpa/spring-data-jpa-example/example.java)

### Querydsl

- [Querydsl의 다양한 예시](jpa/querydsl/querydsl-example/example.java)
- [Projection](jpa/querydsl/projection/content.md)
- [DynamicQuery](jpa/querydsl/dynamic-query/content.md)

## 객체 지향 프로그래밍

- [의존 역전 원칙 - DIP (Dependency Inversion Principle)](oop/dependency-inversion-principal/dip-inflearn/content.md)