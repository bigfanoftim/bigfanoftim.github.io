# JDK, JRE, JVM

## JDK(Java Development Kit)

`JDK(Java Development Kit)`는 Java 프로그램을 개발하기 위해 필요한 도구들의 모음이다. 이 안에는 JRE와 컴파일러, 디버거 등의 개발 도구도 함께 포함되어 있다.

## JRE(Java Runtime Environment)

`JRE(Java Runtime Environment)`는 Java 프로그램을 실행하기 위한 환경을 제공한다. 이 안에는 JVM과 Java 표준 라이브러리가 포함된다. 따라서 Java 프로그램을 실행하려면 JRE가 설치되어 있어야 한다.

## JVM(Java Virtual Machine)

`JVM(Java Virtual Machine)`은 바이트 코드(.class 파일)을 각 OS에 맞게 해석하고 실행하는 역할을 한다.

Java는 우선 JDK에 있는 컴파일러에 의해 바이트 코드로 컴파일된다. 이 바이트 코드는 JVM 위에서 실행되며, JVM은 코드를 OS에 맞게 해석하고 실행한다.

JVM이란 바이트 코드를 실행하는 표준이자 구현체인 셈이다. 우리는 Oracle, Amazon 등의 JVM Vendor가 표준에 맞게 구현한 JVM 구현체를 사용하게 된다.

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
       1: invokespecial #1  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: getstatic     #2  // Field java/lang/System.out:Ljava/io/PrintStream;
       3: ldc           #3  // String Hello, World!
       5: invokevirtual #4  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       8: return
}
```

`javap -c` 명령을 통해 컴파일된 바이트 코드를 사람이 볼 수 있는 형태로 변환하여 출력해준다.