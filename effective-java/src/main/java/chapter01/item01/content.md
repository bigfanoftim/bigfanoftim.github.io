# 생성자 대신 정적 팩토리 메소드를 고려하라

### 참고
- [YES24 - 이펙티브 자바 Effective Java 3/E](https://www.yes24.com/Product/Goods/65551284)
- [인프런 - 이펙티브 자바 완벽 공략 1부](https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#curriculum)

## 정적 팩토리 메소드의 장점 1

```java
public class Order {

    private boolean prime;

    private boolean urgent;

    private Product product;

    public Order(boolean prime, Product product) {
        this.prime = prime;
        this.product = product;
    }

    /**
     * 완전히 동일한 시그니쳐의 생성자가 둘 이상 존재할 수 없기 때문에
     * 매개변수 순서를 바꿔줘야 생성자를 추가로 생성 가능
     */
    public Order(Product product, boolean urgent) {
        this.product = product;
        this.urgent = urgent;
    }
}
```

`primeOrder`가 아닌 `urgentOrder`를 생성하려고 한다면 새로운 생성자가 필요하게 된다. 이때 위와 같이 시그니쳐만 바꿔 새로운 생성자를 추가하는 것이 아니라 정적 팩토리 메소드 사용을 고려하자.

```java
public class Order {

    private boolean prime;

    private boolean urgent;

    private Product product;

    public static Order primeOrder(Product product) {
        Order order = new Order();
        order.prime = true;
        order.product = product;

        return order;
    }

    public static Order urgentOrder(Product product) {
        Order order = new Order();
        order.urgent = true;
        order.product = product;

        return order;
    }
}
```

위와 같이 두 생성자의 시그니쳐가 동일한 경우(동일한 시그니쳐에 대한 컴파일 에러를 방지하기 위해 매개변수 타입의 순서는 바꿨다.) 생성자 대신 정적 팩토리 메소드를 사용하게 되면 `반환하는 객체에 대한 정보를 메소드 이름으로 보다 정확하게 표현하여 가독성을 높일 수 있다`.

## 정적 팩토리 메소드의 장점 2

```java
public class Settings {

    private boolean useAutoSteering;

    private boolean useABS;

    private Difficulty difficulty;

    public static void main(String[] args) {
        System.out.println(new Settings());
        System.out.println(new Settings());
        System.out.println(new Settings());
    }
}
```

정적 팩토리 메소드의 두 번째 장점은 `인스턴스 생성을 관리할 수 있다는 것`이다.

위의 코드를 살펴면 기본 생성자를 3번 호출하여 생성된 인스턴스를 출력하고 있다. 출력 결과는 다음과 같다.

```sh
chapter01.item01.Settings@6f2b958e
chapter01.item01.Settings@5e91993f
chapter01.item01.Settings@1c4af82c
```

이렇게 생성자를 사용하게 되면 항상 새로운 인스턴스를 생성하게 된다.

만약 Settings 인스턴스가 항상 하나만 존재해야 한다면 다음과 같이 정적 팩토리 메소드를 통해 코드를 변경해보자.

```java
// Settings.java
public class Settings {

    private boolean useAutoSteering;

    private boolean useABS;

    private Difficulty difficulty;

    private Settings() {}

    private static final Settings SETTINGS = new Settings();

    public static Settings newInstance() {
        return SETTINGS;
    }
}

// Main.java
public class Main {
    public static void main(String[] args) {
        Settings settings1 = Settings.newInstance();
        Settings settings2 = Settings.newInstance();
        Settings settings3 = Settings.newInstance();

        System.out.println("settings1 = " + settings1);
        System.out.println("settings2 = " + settings2);
        System.out.println("settings3 = " + settings3);
    }
}
```

출력 결과는 다음과 같다.

```sh
settings1 = chapter01.item01.Settings@38cccef
settings2 = chapter01.item01.Settings@38cccef
settings3 = chapter01.item01.Settings@38cccef
```

`private Settings() {}`와 같이 기본 생성자를 private 접근자로 외부 사용을 막음으로써 인스턴스 생성을 Settings 객체 자신이 정적 팩토리 메소드를 통해 관리하게 된다.

따라서 이곳저곳에서 생성자 호출을 통해 다양한 인스턴스가 생성되는 것을 방지할 수 있다.

정적 팩토리 메소드로 인스턴스 생성을 조작하는 대표적인 예시를 살펴보자.

```java
@ValueBased
public final class Boolean implements Serializable, Comparable<Boolean>, Constable {
    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);

    // ...

    @IntrinsicCandidate
    public static Boolean valueOf(boolean b) {
        return b ? TRUE : FALSE;
    }
}
```

위와 같이 Boolean 클래스는 `valueOf`라는 이름의 정적 팩토리 메소드를 통해 미리 생성해놓은 인스턴스를 반환한다.