# MySQL의 격리 수준(Isolation Level)

|                      | DIRTY READ | NON-REPEATABLE READ | PHANTOM READ  |
|----------------------|------------|---------------------|---------------|
| **READ UNCOMMITTED** | O          | O                   | O             |
| **READ COMMITTED**   | X          | O                   | O             |
| **REPEATABLE READ**  | X          | X                   | O (InnoDB: X) |
| **SERIALIZABLE**     | X          | X                   | X             |

- `DIRTY READ`라고도 하는 `READ UNCOMMITTED`는 일반적인 데이터베이스에서 거의 사용 X
- `SERIALIZABLE` 또한 동시성이 중요한 데이터베이스에서는 사용 X
- 뒤로 갈수록 트랜잭션 간의 데이터 격리(고립) 정도가 높아지며, 동시 처리 성능도 떨어지는 것이 일반적
  - 격리 수준이 높아질수록 MySQL 서버의 처리 성능이 많이 떨어질 것으로 생각하지만, `SERIALIZABLE` 격리 수준이 아니라면 크게 성능의 개선이나 저하가 발생하지 않음
- InnoDB의 특성으로 `REPEATABLE READ` 격리 수준에서 `PHANTOM READ`가 발생하지 않음
- 온라인 서비스 용도에서는 일반적으로 `READ COMMITTED` 혹은 `REPEATABLE READ` 격리 수준을 많이 사용

## READ UNCOMMITTED
- 이 격리 수준에서는 트랜잭션의 변경 내용이 커밋, 롤백고 상관없이 다른 트랜잭션에서 보이게 됨
- 예를 들어, `Jun`이라는 회원을 Insert하고 아직 커밋이 안된 시점에 다른 트랜잭션에서 해당 회원을 조회하게 되면 조회가 됨
- 이렇게 되면 Insert 쿼리를 실행한 트랜잭션이 롤백이 되어도 이미 해당 회원을 조회한 트랜잭션은 올바른 회원이라고 인식하게 되는 문제 발생

### 🚨 DIRTY READ
- 위와 같이 트랜잭션에서 처리한 작업이 완료되지 않았음에도 다른 트랜잭션에서 볼 수 있는 현상
- `READ UNCOMMITTED` 격리 수준에서만 나타나며 RDBMS 표준에서는 이 격리 수준을 트랜잭션의 격리 수준으로 인정하지 않을 정도로 데이터 정합성에 문제가 많음
- MySQL을 사용한다면 최소한 `READ COMMITTED` 이상의 격리 수준을 사용하여 DIRTY READ를 방지하자.

## READ COMMITTED
- 오라클 DBMS에서 기본으로 사용되는 격리 수준
- 온라인 서비스에서 가장 많이 사용됨

### 예시
- `employees` 테이블에 `emp_no=5`인 데이터가 존재한다고 가정
- 사용자 A가 트랜잭션을 시작해 `emp_no=5`인 사원의 이름을 `BEFORE`에서 `AFTER`로 변경 (커밋 전)
  - 이때 변경 전의 데이터인 `BEFORE` 사원 데이터를 언두 로그에 백업
  - 새로운 이름인 `AFTER`는 곧바로 테이블에 저장
- 사용자 B가 `emp_no=5`를 조회(언두 로그에서 조회), 해당 사원의 이름은 `BEFORE`로 출력됨
  - `READ COMMITTED` 격리 수준에서는 다른 트랜잭션의 변경사항을 커밋되기 전까지 볼 수 없기 때문

### 🚨 NON-REPEATABLE READ
- `READ COMMITTED` 격리 수준에서는 `DIRTY READ`는 발생하지 않지만 `REPEATABLE READ`가 불가능한 문제가 있음
- 예를 들어, `employees` 테이블에 `emp_no=5`인 데이터가 존재한다고 가정 
  - 사용자 A가 트랜잭션을 시작하고 `emp_no=5`인 데이터를 조회, 이때 조회된 사원의 이름은 `JAVA` (커밋 전)
  - 이때 사용자 B가 `emp_no=5`인 사원의 이름을 `PYTHON`으로 변경 후 커밋
  - 사용자 A가 트랜잭션 안에서 다시 `emp_no=5`인 데이터를 조회, 이때 조회된 사원의 이름은 `PYTHON`
- 이렇게 같은 트랜잭션 내에서 똑같은 SELECT 쿼리를 실행했을 때는 항상 같은 결과를 가져와야 한다는 `REPEATABLE READ` 정합성에 어긋남
- 이런 문제로 데이터의 정합성이 깨져 버그가 발생하면 찾아내기가 쉽지 않음

## REPEATABLE READ
- MySQL의 InnoDB 스토리지 엔진에서 기본으로 사용되는 격리 수준
- InnoDB의 트랜잭션은 고유 번호를 가지고 있으며, 언두 영역에 저장된 백업 레코드는 트랜잭션의 고유 번호를 함께 저장함
- 그리고 쿼리를 호출하는 트랜잭션의 번호보다 더 낮은 트랜잭션 번호에서 변경된 레코드만 읽기 때문에 항상 일관된 레코드를 반환하게 됨

### 🚨 PHANTOM READ
- 트랜잭션 번호 10번에서 처음 조회했을 때 보이지 않았던 데이터가 이후에 다시 조회했는데 보이게 되는 것과 같이 다른 트랜잭션에서 수행한 변경 작업에 의해 레코드가 보였다 안보였다 하는 현상
- `SELECT ... FOR UPDATE`와 같은 쿼리는 SELECT하는 레코드에 쓰기 잠금을 걸어야 하는데 언두 레코드에는 잠금을 걸 수가 없음
- 따라서 이런 쿼리는 언두 영역의 변경 전 데이터를 가져오는 것이 아니라 현재 레코드의 값을 가져오게 됨

### 그러나 InnoDB에서는..
레코드 락, 갭 락의 결합인 넥스트 키 락을 구현하기 때문에 해당 범위 내의 레코드 변경과 삽입을 모두 방지하게 된다.

그에 따라 `REPEATABLE READ` 격리 수준이라 하더라도 `PHANTOM READ` 현상이 발생하지 않는다고 볼 수 있다.

그러나 넥스트 키 락은 결국 적절한 인덱스를 필요로 하기 때문에 InnoDB에서도 `PHANTOM READ` 현상이 발생할 수 있다.

대신 조회에서 사용되는 기준을 통해 적절히 인덱스를 추가하면 해결되는 문제이다.

```sql
SELECT * FROM students WHERE age = 20;
```

위와 같은 조회에서 넥스트 키 락의 효과를 보고 싶다면 아래와 같이 age 컬럼에 적합한 인덱스를 추가해줘야 한다.

```sql
CREATE INDEX idx_age ON students(age);
```

만약 아래와 같은 복합 인덱스가 설정되어 있다면 age 필드를 기준으로 조회하는 경우 잠금 효과를 보지 못할 수 있다.
```sql
CREATE INDEX idx_name_age ON students(name, age);

SELECT * FROM students WHERE age = 20;
```

## SERIALIZABLE
- 가장 단순하면서 가장 엄격한 격리 수준
- 동시 처리 성능 최하
- InnoDB 테이블에서는 기본적으로 순수 SELECT 쿼리의 경우 아무런 레코드 잠금없이 실행됨 (Non-locking consistent read)
- `SERIALIZABLE` 격리 수준에서는 읽기 작업도 공유 잠금(읽기 잠금)을 획득하며 다른 트랜잭션에서 해당 레코드를 변경하지 못하게 됨
- 따라서 일반적인 DBMS에서 일어나는 `PHANTOM READ` 현상이 발생하지 않음
- 하지만 InnoDB 스토리지 엔진에서는 갭 락, 넥스트 키 락 덕분에 `REPEATABLE READ` 격리 수준에서도 `PHANTOM READ` 현상이 발생하지 않기 때문에 굳이 `SERIALIZABLE` 격리 수준을 사용할 필요가 없음

### 참고
- [Real MySQL 8.0](https://www.yes24.com/Product/Goods/103415627)
