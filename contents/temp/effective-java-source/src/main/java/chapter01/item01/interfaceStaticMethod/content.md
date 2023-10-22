# 인터페이스와 정적 메소드

### 참고
- [YES24 - 이펙티브 자바 Effective Java 3/E](https://www.yes24.com/Product/Goods/65551284)
- [인프런 - 이펙티브 자바 완벽 공략 1부](https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#curriculum)

## 인터페이스에 메소드 정의

Java 8부터는 인터페이스에 메소드를 선언하는 것뿐만 아니라 정의하는 것이 가능하다.

인스턴스 메소드를 정의하려면 `default` 키워드를 사용해야 한다. 인스턴스 없이도 호출할 수 있는 정적 메소드도 `static` 키워드를 통해 정의가 가능하다.

```Java
public interface HelloService {

    // 선언
    String hello();

    // 정적 메소드
    static String hi() {
        return "hi";
    }

    // 인스턴스 메소드
    default String bye() {
        return "bye";
    }
}
```

## private static 메소드 정의

Java 9부터는 private static 메소드 또한 정의할 수 있다.

```Java
public interface HelloService {

    String hello();

    static String hi() {
        prepareMessage();
        return "hi";
    }

    static String hi2() {
        prepareMessage();
        return "hi";
    }

    static String hi3() {
        prepareMessage();
        return "hi";
    }

    static private void prepareMessage() {
    }
}
```

