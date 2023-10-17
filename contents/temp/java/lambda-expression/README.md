# 람다식(Lambda Expression)

```java
(int a, int b) -> a > b ? a : b;
```

```java
new Object() {
    int max(int a, int b) {
        return a > b ? a : b;
    }
}
```

람다식은 익명 클래스의 객체와 동등하다.

```java
public interface MyFunction {
    public abstract int max(int a, int b);
}
```

```java
public class Main {
    public static void main(String[] args) {
        MyFunction f = new MyFunction() {
            public int max(int a, int b) {
                return a > b ? a : b;
            }
        };
        int max = f.max(5, 3);
    }
}
```

- MyFunction 인터페이스를 구현한 익명 클래스의 객체를 활용
- 아래와 같이 작성 가능

```java
public class Main {
    public static void main(String[] args) {
        MyFunction f2 = (int a, int b) -> a > b ? a : b;
        int max2 = f2.max(10, 1);
    }
}
```

### 참고
- [YES24 - Java의 정석](https://www.yes24.com/Product/Goods/24259565)

