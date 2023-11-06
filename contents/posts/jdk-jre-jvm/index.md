---
title: "JDK, JRE, 그리고 JVM에 대한 이해"
description: ""
date: 2023-08-24
update: 2023-11-03
tags:
  - java
  - jdk
  - jre
  - jvm
series: "java"
---

## JDK, JRE, 그리고 JVM에 대한 이해

Java를 통한 프로그래밍 및 애플리케이션 구동에 있어 JDK, JRE, 그리고 JVM은 핵심 구성 요소로 작용한다.
이들의 역할을 정확하게 이해하는 것은 Java 프로그래밍을 하는데 있어 매우 중요하다.

### JDK(Java Development Kit)

`JDK(Java Development Kit)`는 Java 프로그램을 개발하기 위해 필요한 도구들의 모음이다. 
이 안에는 JRE와 컴파일러, 디버거 등의 개발 도구도 함께 포함되어 있어 개발자가 Java로 작성된 코드를
컴파일, 디버깅, 실행할 수 있다.

### JRE(Java Runtime Environment)

`JRE(Java Runtime Environment)`는 Java 프로그램을 실행하기 위한 환경을 제공한다. 
이 안에는 JVM과 Java 표준 라이브러리가 포함된다. 따라서 Java 프로그램을 실행하려면 JRE가 설치되어 있어야 한다.

### JVM(Java Virtual Machine)

`JVM(Java Virtual Machine)`은 바이트 코드(.class 파일)을 각 OS에 맞게 해석하고 실행하는 역할을 한다.

Java는 우선 JDK에 있는 컴파일러에 의해 바이트 코드로 컴파일된다. 
바이트 코드는 JVM 위에서 실행되며, JVM은 코드를 OS에 맞게 해석하고 실행한다.
이는 Java 프로그램이 한 번 작성되면 어떤 플랫폼에서도 실행될 수 있도록 해주는 `Write Once, Run Anywhere` 원칙을 가능하게 한다.

JVM이란 바이트 코드를 실행하는 표준이자 구현체인 셈이다. 
우리는 Oracle, Amazon 등의 JVM Vendor가 표준에 맞게 구현한 JVM 구현체를 사용하게 된다.

```java
// Hello.java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

만약 다음과 같은 Java 파일을 컴파일하여 바이트 코드로 변환하고 `javap -c Hello.class` 명령어를 실행하면 다음과 같은 결과를 볼 수 있다.

```
Compiled from "Hello.java"
public class Hello {
  public Hello();
    Code:
       0: aload_0
       1: invokespecial ##1  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: getstatic     ##2  // Field java/lang/System.out:Ljava/io/PrintStream;
       3: ldc           ##3  // String Hello, World!
       5: invokevirtual ##4  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       8: return
}
```

`javap -c` 명령을 통해 컴파일된 바이트 코드를 사람이 볼 수 있는 형태로 변환하여 출력해 주는 것이다.


컴파일된 바이트 코드 즉, 생성된 class 파일을 위의 이미지와 같은 순서로 JVM이 처리하게 된다.

### JVM 구조

![jvm-architecture.png](jvm-architecture.png)

JVM은 다음과 같이 크게 4가지 영역으로 나눠져 있다.

1. 클래스 로더(Class Loader)
2. 메모리(JVM Memory)
3. 실행 엔진(Execution Engine)
4. JNI(Java Native Interface) - 네이티브 메소드 인터페이스, 네이티브 메소드 라이브러리

#### 클래스 로더(Class Loader)

클래스 로더는 .class 파일의 바이트 코드를 읽어 메모리(JVM Memory)에 저장하는 역할을 한다.

#### 메모리(JVM Memory)

클래스 로더를 통해 다음과 같이 메모리에 바이트 코드의 정보를 저장한다.
- `메소드(Method Area)` 영역에 클래스 수준의 정보를 저장
- `힙(Heap)` 영역에 객체를 저장


## 참고
- [How JVM Works – JVM Architecture?](https://www.geeksforgeeks.org/jvm-works-jvm-architecture/)
