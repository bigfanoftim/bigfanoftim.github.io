# 생성자 대신 정적 팩토리 메소드를 사용하는 것의 5가지 장점

### 참고
- [YES24 - 이펙티브 자바 Effective Java 3/E](https://www.yes24.com/Product/Goods/65551284)
- [인프런 - 이펙티브 자바 완벽 공략 1부](https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#curriculum)

## 첫 번째, 이름을 가질 수 있다.

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

`primeOrder`가 아닌 `urgentOrder`를 생성하려고 한다면 새로운 생성자가 필요하게 된다. 
이때 위와 같이 시그니쳐만 바꿔 새로운 생성자를 추가하는 것이 아니라 정적 팩토리 메소드 사용을 고려하자.

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

위와 같이 두 생성자의 시그니쳐가 동일한 경우(동일한 시그니쳐에 대한 컴파일 에러를 방지하기 위해 매개변수 타입의 순서는 바꿨다.) 
생성자 대신 정적 팩토리 메소드를 사용하게 되면 `반환하는 객체에 대한 정보를 메소드 이름으로 보다 정확하게 표현하여 가독성을 높일 수 있다`.

## 두 번째, 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.

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

`private Settings() {}`와 같이 기본 생성자를 private 접근자로 외부 사용을 막음으로써 인스턴스 생성을 
Settings 객체 자신이 정적 팩토리 메소드를 통해 관리하게 된다.

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

## 세 번째, 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.

```java
public class HelloServiceFactory {

    public static HelloService of(String lang) {
        if (lang.equals("ko")) {
            return new KoreanHelloService();
        } else {
            return new EnglishHelloService();
        }
    }
}
```

```java
public class KoreanHelloService implements HelloService {

    @Override
    public String hello() {
        return "안녕하세요.";
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        HelloService koreanHelloService = HelloServiceFactory.of("ko");
        String hello = koreanHelloService.hello();
        System.out.println("hello = " + hello); // hello = 안녕하세요.
    }
}
```

위와 같이 구체적인 구현을 클라이언트에게 숨기고 인터페이스를 활용하도록 강제하여 유연한 설계가 가능해진다.

## 네 번째, 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.

세 번째 장점의 예시에서 네 번째 장점까지 모두 설명이 가능하다.

추가로 Java 8부터 인터페이스에 static method를 추가할 수 있으니 굳이 Factory 클래스를 또 추가하지 말고 
인터페이스에 static method를 추가하는 방향으로 리팩토링 해보자.

```java
public interface HelloService {

    String hello();

    static HelloService of(String lang) {
        if (lang.equals("ko")) {
            return new KoreanHelloService();
        } else {
            return new EnglishHelloService();
        }
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        HelloService eng = HelloService.of("eng");
        String hello = eng.hello();
        System.out.println("hello = " + hello); // hello = Hello!
    }
}
```

## 다섯 번째, 정적 팩토리 메소드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

Java가 기본적으로 제공하는 정적 팩토리 메소드 `ServiceLoader.load()`를 사용하여 다섯 번째 장점에 대해 살펴보자.

```java
public class Main {
    public static void main(String[] args) {
        ServiceLoader<HelloService> load = ServiceLoader.load(HelloService.class);
        Optional<HelloService> helloServiceOptional = load.findFirst();
        helloServiceOptional.ifPresent(helloService -> {
            System.out.println(helloService.hello());
        });
    }
}
```

구현체에 직접 의존하지 않고 위와 같이 인터페이스에만 다루는 유연한 형태로 설계할 수 있다.