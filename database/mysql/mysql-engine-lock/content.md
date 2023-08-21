# MySQL 엔진의 잠금

> 이 글은 [Real MySQL 8.0](https://www.yes24.com/Product/Goods/103415627) 책을 참고하여 작성되었습니다.

- MySQL에서 사용되는 잠금
  - 스토리지 엔진 레벨 -> 스토리지 엔진 간 상호 영향 X
  - MySQL 엔진 레벨 -> 모든 스토리지 엔진에 영향 

### 글로벌 락(GLOBAL LOCK)
- `FLUSH TABLES WITH LOCK` 명령으로 획득
- MySQL에서 제공하는 잠금 가운데 범위가 가장 크다.
- 글로벌 락은 실행과 동시에 MySQL에 존재하는 모든 테이블을 닫고 잠그기 때문에 웹 애플리케이션에서는 가급적 사용 X 
- `mysqldump`와 같은 백업 프로그램이 이 명령을 내부적으로 실행할 수도 있음
- 8.0 버전부터는 조금 더 가벼운 락인 백업 락이 도입됨

### 테이블 락(TABLE LOCK)
- 개별 테이블 단위로 설정되는 잠금
- `LOCK TABLES table_name ...` 명령으로 특정 테이블의 락을 획득
- `UNLOCK TABLES` 명령으로 잠금 해제
- InnoDB 테이블의 경우 묵시적인 테이블 락이 단순 데이터 변경 쿼리로 설정되지 않는다.
  - 대부분의 데이터 변경(DML) 쿼리에서는 무시, 스키마 변경(DDL) 쿼리의 경우에만 영향

### 네임드 락(NAMED LOCK)
- `GET_LOCK()` 함수를 이용하여 임의의 문자열에 대해 잠금 설정(데이터베이스 객체가 대상이 아님)
- 여러 웹 서비스에서 접속하여 서비스하는 상황에서 여러 클라이언트끼리 동기화 처리가 필요한 경우 쉽게 문제를 해결할 수 있음

  ```mysql
  -- "my_lock"이라는 문자열에 대해 잠금 획득
  -- 이미 잠금 사용 중이면 2초 동안 대기 (2초 이후 자동 잠금 해제)
  SELECT GET_LOCK('my_lock', 2);

  -- "my_lock"이라는 문자열에 대해 잠금 설정되었는지 확인
  SELECT IS_FREE_LOCK('my_lock');

  -- 잠금 해제
  SELECT RELEASE_LOCK('my_lock');
  ```

- 많은 레코드를 변경하는 트랜잭션에 유용
  - 배치 프로그램처럼 한꺼번에 많은 레코드를 변경하는 쿼리는 잦은 데드락의 원인
  - 이런 경우 동일 데이터를 변경하거나 참조하는 프로그램끼리 분류하여 네임드 락을 걸고 쿼리를 실행하면 간단히 해결됨
- 8.0 버전부터는 중첩 네임드 락 사용 및 현재 세션에서 획득한 모든 락을 해제하는 기능 추가

  ```mysql
  -- 1에 대해 잠금
  SELECT GET_LOCK('my_lock_1', 10);
  -- 1과 2에 대해 모두 작업 실행
  SELECT GET_LOCK('my_lock_2', 10);
  
  -- 1, 2 동시 해제
  SELECT RELEASE_ALL_LOCKS();
  ```
  
### 메타데이터 락(METADATA LOCK)
- 데이터베이스 객체의 이름이나 구조 변경 시 획득하는 잠금
- 명시적으로 획득, 해제하는 것이 아닌 `RENAME TABLE table_name TO new_table_name`와 같은 명령어 실행 시 자동으로 잠금 획득
- `RENAME` 명령의 경우 원본 이름, 변경될 이름 모두 한꺼번에 잠금 설정됨
- 실시간으로 테이블을 바꿔야 하는 요건이 배치 프로그램에서 자주 발생
  ```mysql
  RENAME TABLE rank TO rank_backup, rank_new TO rank;
  ```
  - 위와 같이 작성하면 애플리케이션에서 `Table not found 'rank'`와 같은 상황을 발생시키지 않는다.
  - 하지만 두 명령으로 나누게 되면 짧은 시간이지만 rank 테이블이 존재하지 않는 순간이 생겨 not found 에러가 발생한다.
    ```mysql
    RENAME TABLE rank TO rank_backup;
    RENAME TABLE rank_new TO rank;
    ```
- TODO: 메타데이터 잠금과 InnoDB 트랜잭션 동시 이용 케이스