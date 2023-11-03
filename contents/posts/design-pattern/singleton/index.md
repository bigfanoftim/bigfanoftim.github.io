---
title: "싱글턴(Singleton) 패턴과 구현"
description: ""
date: 2023-11-03
update: 2023-11-03
tags:
- java
- design-pattern
- singleton
- oop
- synchronized
- dcl
- volatile
series: "design-pattern"
---

## 싱글턴(Singleton) 패턴이란?

싱글턴 패턴은 특정 클래스에 인스턴스가 하나만 만들어지도록 하는 패턴이다. 

전역 변수를 사용할 때와 마찬가지로 객체 인스턴스를 어디서든지 액세스할 수 있게 만들 수 있으며, 전역 변수를 쓸 때처럼 여러 단점을 감수할 필요가 없다.

전역 변수에 객체를 대입하면 애플리케이션이 시작될 때 객체가 생성된다. 만약 이 객체가 자원을 많이 차지하고 
애플리케이션이 끝날 때까지 한 번도 쓰이지 않는다면 자원만 잡아먹는 쓸데없는 객체가 되는 것이다.
하지만 싱글턴 패턴을 사용하면 필요할 때만 객체를 만들 수 있다.

## 간단하게 싱글턴 패턴 구현

```java
public class Singleton {
    
    private static Singleton uniqueInstance;
    
    private Singleton() {} // (1)
    
    public static Singleton getInstance() { // (2)
        if (uniqueInstance == null) {
            uniqueInstance = new Singleton();
        }
        return uniqueInstance;
    }
}
```

`(1)`에서 생성자를 `private`으로 선언하여 정적 메소드를 통해서만 클래스의 인스턴스를 만들 수 있다.

`(2)`는 클래스의 인스턴스를 리턴하는 정적 메소드이다. 
`uniqueInstance`가 `null`이면 아직 인스턴스가 생성되지 않았다는 것이다. 따라서 새로운 객체를 생성하여 리턴한다. (`new Singleton()`)

## 멀티스레딩 문제

만약 위와 같은 간단한 구현에서 여러 스레드가 `Singleton.getInstance()` 메소드를 동시에 실행한다고 가정해 보자. 
각 스레드에서 `uniqueInstance`가 `null`임을 알고 새로운 객체 인스턴스를 반환하는 문제가 발생할 것이다. 

지금부터 이러한 멀티스레딩 환경에서의 문제를 어떻게 해결하는지 살펴보자.

### synchronized

Java에서 지원하는 `synchronized` 키워드로 메소드를 동기화하면 위의 문제는 간단하게 해결된다.

```java
public class Singleton {

    private static Singleton uniqueInstance;

    private Singleton() {}

    public static synchronized Singleton getInstance() { // (1)
        if (uniqueInstance == null) {
            uniqueInstance = new Singleton();
        }
        return uniqueInstance;
    }
}
```
`(1)`과 같이 `synchronized` 키워드만 추가하면 한 스레드가 해당 메소드 사용을 끝내기 전까지 다른 스레드는 대기해야 하므로 
멀티스레딩 문제가 해결된다.

다만 `synchronized` 키워드를 활용한 동기화는 해당 키워드가 추가된 메소드를 실행한 스레드를 제외한 모든 스레드가 기다리기 때문에 성능 문제가 있을 수 있다.
게다가 객체 인스턴스를 처음에 생성할 때에만 조심하면 되는 것임에도 불구하고 계속해서 동기화되는 것이기 때문에 불필요한 오버헤드만 증가시킬 뿐이다.

### 처음에 미리 생성

인스턴스를 실행 중에 수시로 만들고 관리하기가 어렵다면 다음과 같이 처음부터 인스턴스를 만들자.

```java
public class Singleton {
    
    private static Singleton uniqueInstance = new Singleton();
    
    private Singleton() { }
    
    public static Singleton getInstance() {
        return uniqueInstance;
    }
}
```

이렇게 처음에 미리 생성하는 방식을 선택하면 클래스가 로딩될 때 JVM에서 오직 하나의 인스턴스를 생성해 준다.

### DCL(Double-Checked Locking)

DCL을 사용하면 동기화되는 부분을 줄여 초기화 단계에서만 동기화를 진행하고 나중에는 동기화하지 않아도 된다.

```java
public class Singleton {

    private volatile static Singleton uniqueInstance; // (1) volatile

    private Singleton() { }

    public static Singleton getInstance() {
        if (uniqueInstance == null) {
            synchronized (Singleton.class) { // (2)
                if (uniqueInstance == null) { // (3)
                    uniqueInstance = new Singleton();
                }
            }
        }
        return uniqueInstance;
    }
}
```

`(1)`의 `volatile` 키워드를 사용하여 한 스레드가 변경한 값을 다른 스레드에서도 곧바로 반영된다.

`(2)`와 같이 이전에 미리 `uniqueInstance`가 `null`인 경우에만 동기화를 진행하여 효율을 높인다.
그리고 `(3)`에서 다시 `null` 체크를 하여 다른 스레드에서 값을 반영했는지 확인한다.

이렇게 DCL을 활용하면 간단하게 `synchronized`를 활용했을 때보다 동기화하는 영역이 적기 때문에 효율을 높이면서
멀티스레딩 환경에서의 문제를 해결할 수 있다.

### enum

옛날 옛적과는 다르게 현대 Java는 위의 모든 것을 해결해 줄 `enum`이란 것을 제공한다.
`enum`을 사용하면 정말 간편하게 많은 문제를 해결할 수 있다.

그럼 `enum`을 활용한 간단한 싱글턴 패턴 예시를 살펴보며 마무리하자.

```java
public enum Configuration {
    INSTANCE;

    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public void loadConfiguration() {
        this.dbUrl = "jdbc:mysql://localhost:3306/myDatabase";
        this.dbUser = "user";
        this.dbPassword = "password";
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}
```

```java
public class Application {
    public static void main(String[] args) {
        Configuration.INSTANCE.loadConfiguration();
        
        System.out.println("DB URL: " + Configuration.INSTANCE.getDbUrl());
        System.out.println("DB User: " + Configuration.INSTANCE.getDbUser());
    }
}
```

## 참고
- [YES24 - 헤드 퍼스트 디자인 패턴](https://www.yes24.com/Product/Goods/108192370)