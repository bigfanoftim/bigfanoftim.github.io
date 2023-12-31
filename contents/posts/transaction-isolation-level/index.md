---
title: "Isolation Level In MySQL"
description: "MySQL의 격리 수준에 대하여"
date: 2023-08-22
update: 2023-12-17
tags:
  - mysql
  - isolation-level
series: "database"
---

이번 포스팅에서는 MySQL의 격리 수준(isolation level)에 대해 알아보고자 한다. 여기서 격리 수준이란, 트랜잭션간의 격리 수준을 의미한다. 
격리 수준을 살펴보기 전에 먼저 `DIRTY READ`, `NON-REPEATABLE READ`, `PHANTOM READ` 현상이 각각 무엇을 의미하는지부터 살펴보자.

## 격리 수준에 따라 발생하는 현상들

|                      | DIRTY READ | NON-REPEATABLE READ | PHANTOM READ  |
|----------------------|------------|---------------------|---------------|
| **READ UNCOMMITTED** | O          | O                   | O             |
| **READ COMMITTED**   | X          | O                   | O             |
| **REPEATABLE READ**  | X          | X                   | O (InnoDB: X) |
| **SERIALIZABLE**     | X          | X                   | X             |

먼저 `DIRTY READ`, `NON-REPEATABLE READ`, `PHANTOM READ` 현상이 각각 무엇을 의미하는지부터 살펴보자.

### DIRTY READ
`DIRTY READ`는 트랜잭션의 작업이 완료(커밋)되지 않았음에도 다른 트랜잭션에서 내용을 볼 수 있는 현상을 의미한다.
이는 데이터 정합성의 많은 문제가 있으니, MySQL을 사용한다면 최소한 `DIRTY READ`가 발생하지 않는 격리 수준을 사용하자.

### NON-REPEATABLE READ
`NON-REPEATABLE READ`란 말 그대로 `반복할 수 없는 읽기`이다. 특정 행을 반복해서 조회할 때 그 내용이 달라지는 것을 의미한다.
A 트랜잭션에서 같은 행을 반복해서 조회하는데 그 조회 내용이 달라진다는 것은 곧 현재 격리 수준이 새롭게 커밋된 데이터를 읽을 수 있음을 의미한다.

### PHANTOM READ
`PHANTOM READ`는 다른 트랜잭션에 의해 데이터가 보였다 안 보였다 하는 현상을 의미한다.
`NON-REPEATABLE READ`의 한 종류라고 볼 수 있고 그 말인즉슨 `NON-REPEATABLE READ`가 발생하는 격리 수준이라면 `PHANTOM READ`도 발생한다.


## 격리 수준(isolation level)

### READ UNCOMMITTED
이 격리 수준에서는 커밋되지 않은 다른 트랜잭션의 내용까지 볼 수 있다.
예를 들어, `Jun`이라는 회원을 Insert하고 아직 커밋을 하지 않았을 때도 다른 트랜잭션에서 `Jun`이라는 커밋되지 않은 회원을 볼 수 있다.

따라서 위에서 언급한 모든 현상이 발생한다.

### READ COMMITTED
`READ COMMITTED`는 오라클 DBMS에서 기본으로 사용되는 격리 수준이며 온라인 서비스에서 가장 많이 사용된다고 한다.

이름에서도 알 수 있듯이 커밋된 내용만 읽는다. 따라서 `DIRTY READ`는 발생하지 않는다. 다만 커밋된 내용이라면 항상 읽어드리기 때문에
같은 데이터를 여러 번 조회했을 때 새롭게 업데이트된 내용이 있다면 조회 결과가 달라지는 `NON-REPEATABLE READ`가 발생하게 된다.

### REPEATABLE READ
`REPEATABLE READ`는 MySQL의 InnoDB 스토리지 엔진에서 기본으로 사용되는 격리 수준이다.
이 격리 수준이 어떻게 `NON-REPEATABLE READ` 현상을 방지하는지 이해하려면 먼저 InnoDB 동작에 대한 이해가 필요하다.

InnoDB 스토리지 엔진은 트랜잭션이 롤백 될 가능성에 대비하여 변경되기 전의 레코드를 언두 영역에 백업하고 실제 레코드값을 변경한다.
또한 InnoDB의 트랜잭션은 고유 번호를 가지고 있으며 언두 레코드는 저장 시 트랜잭션의 고유 번호를 함께 저장한다. 

InnoDB는 데이터를 변경했을 때 언두 영역에 이전 레코드를 보관하는 것뿐만 아니라 새로운 데이터를 생성하는 순간에도 사용자 정의 
스키마가 아닌 InnoDB 스토리지 엔진이 내부적으로 관리하는 곳에 트랜잭션에 대한 정보를 저장한다. 예를 들어, `TX_ID: 6`인 트랜잭션이 `Jun`이라는
회원 데이터를 테이블에 삽입했다고 가정하자. 그렇다면 이후에 다른 트랜잭션들은 `Jun`이라는 회원 데이터를 조회할 때 해당 데이터를 관리했던 트랜잭션에 대한
정보도 함께 알 수 있다.

그럼, 이제 위의 내용들을 기반으로 실제로 어떻게 `NON-REPEATABLE READ` 현상을 방지할 수 있는지 예시를 통해 살펴보자.
1. 현재 회원 테이블에는 `TX_ID: 6`인 트랜잭션이 삽입한 `Jun` 회원 데이터가 존재한다.
2. 사용자 B가 `TX_ID: 10` 트랜잭션을 시작하여 `Jun` 회원 데이터를 조회한다. 이때 정상적으로 `Jun`이라는 데이터가 보인다. 자신의 트랜잭션보다 최신의 트랜잭션이 건드린 데이터가 아님을 알고 정상적으로 조회한다.
3. 이때 사용자 A가 `TX_ID: 12` 트랜잭션을 시작하여 `Jun` 회원의 이름을 `Andy`로 수정하고 커밋한다. 이 과정에서 실제 레코드값을 수정하고, `TX_ID: 12`인 트랜잭션이 이를 수정했다는 정보를 InnoDB가 내부적으로 저장한다. 그리고 변경 전 값을 언두 영역에 `TX_ID: 6`과 함께 저장한다.
4. 다시 사용자 B가 `TX_ID: 10` 트랜잭션에서 해당 회원을 조회하게 되면 이제 실제 레코드에는 `TX_ID: 12`가 수정한 값이 존재하니 언두 영역에서 최신 데이터가 아닌 `TX_ID: 6`이 작업한 `Jun`이라는 데이터를 조회해 온다.

위와 같이 `REPEATABLE READ` 격리 수준은 `SELECT`로 데이터 조회 시 언두 영역에 백업된 이전 데이터를 이용해 동일한 트랜잭션 내에서는 항상 같은 
값을 보여주어 `NON-REPEATABLE READ` 현상을 방지한다.

추가로 InnoDB 사용 시 `REPEATABLE READ` 격리 수준에서 데이터가 보였다 안 보였다 하는 현상인 `PHANTOM READ` 현상도 방지할 수 있다.
테이블에 적절한 인덱스가 추가되었다면 `SELECT ... FOR UPDATE` 쿼리를 통해 적절한 인덱스 락이 걸리기 때문에 데이터를 삽입할 수 없게 되어
`PHANTOM READ` 현상을 막을 수 있다.

> `SELECT ... FOR UPDATE` 쿼리는 새로운 레코드가 아닌 기존에 존재하는 레코드에 대한 변경을 제어하는 목적으로 사용된다.
> 따라서 직접 새로운 레코드가 삽입되는 것에 대한 잠금을 거는 것은 아니다. 또한 `SELECT` 쿼리는 트랜잭션 시작 시점의 스냅샷을 기반으로 언두 로그를 참조하여
> 트랜잭션 시작 시점의 데이터만 조회하는 것으로 새로운 데이터 삽입 혹은 변경에 대한 영향을 받지 않는데, `SELECT ... FOR UPDATE` 쿼리는 기본적으로
> 트랜잭션 스냅샷과 상관없이 최신의 데이터를 조회하게 된다. 따라서 적절한 인덱스 락을 사용하지 못하면 새로운 데이터의 추가를 막지 못해 
> 결국 `PHANTOM READ` 현상이 발생할 수 있다.

### SERIALIZABLE
`SERIALIZABLE`은 가장 단순하면서도 엄격한 격리 수준이다. InnoDB에서는 기본적으로 `SELECT` 쿼리에 대해 읽기 잠금을 사용하지 않는다. (Non-locking consistent read)
다만 이 격리 수준에서는 읽기 작업도 잠금을 획득하여 다른 트랜잭션에서 해당 레코드에 대한 수정, 삽입이 불가능해진다.

하지만 InnoDB 스토리지 엔진에서는 위에서 언급했던 것처럼 `REPEATABLE READ` 격리 수준에서도 `PHANTOM READ` 현상이 발생하지 않기 때문에 굳이
이 격리 수준을 사용할 필요가 없다.

## 참고
- [Real MySQL 8.0](https://www.yes24.com/Product/Goods/103415627)
