# equals 메소드의 오버라이딩

## 참조 비교가 아닌 속성 비교
Java의 `Object` 클래스의 `eqauls` 메소드는 기본적으로 두 객체의 메모리 주소(참조)를 비교한다.
즉 `==` 연산자와 동일한 동작을 수행한다. 하지만 상황에 따라 두 객체의 참조가 아닌 속성을 기반으로 비교해야 할 필요가 있을 수 있다.

예를 들어, 두 `Person` 객체가 같은 이름과 나이를 갖고 있다면, 비즈니스 로직 상에서 두 객체를 같은 사람으로 취급하려고 한다.
이때 `equals` 메소드를 오버라이딩하지 않고 그대로 활용하게 되면 `john1`, `john2`는 같은 사람이 아니게 된다.

```java
public class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

```java
public class Main {

    public static void main(String[] args) {
        Person john1 = new Person("John", 30);
        Person john2 = new Person("John", 30);

        System.out.println(john1 == john2); // false 출력
        System.out.println(john1.equals(john2)); // false 출력
    }
}
```

그럼 `equals` 메소드를 오버라이딩하여 참조를 비교하는 것이 아니라 속성을 비교하도록 수정해보자.

```java
public class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
```

위의 코드는 IntelliJ IDEA의 도움으로 간단하게 오버라이딩한 결과이다. 오버라이딩한 `equals` 메소드를 자세히 살펴보면
`if (!(o instanceof Person)) return false;` 코드를 볼 수 있다.
`Person` 클래스의 인스턴스가 아니면 `false`를 리턴하는 코드이다.
하지만 `instanceof` 연산자는 특정 클래스 또는 그 하위 클래스의 인스턴스인지 확인하기 때문에 하위 클래스의 객체가 상위 클래스로 형변환되어 들어온 경우에도
`equals` 메소드는 `true`를 반환하게 되는 문제가 있다.

### getClass() 메소드로 정확한 클래스 일치 확인
상황에 따라 `instanceof` 연산자 대신 정확한 클래스의 일치 확인을 위해 아래 코드처럼 `getClass()`를 사용하는 것이 좋은 방법이 될 수 있다.

```java
public class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
```

## String 클래스의 equals 메소드
위의 `equals` 메소드를 다시 살펴보면 `Objects.equals`를 통해 String 객체를 직접 비교하고 있다.
`Objects.equals`는 내부에서 참조와 속성 비교 및 `null` 체크까지 하기 때문에 안전하다.

```java
public final class Objects {
    // ...

    public static boolean equals(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    // ...
}
```

근데 `a.equals(b)`는 결국 참조를 비교하는 것이 아닌가 라고 생각할 수 있지만,
대부분의 라이브러리나 사용자 정의 객체에서는 `equals` 메소드를 오버라이딩하여 속성을 비교하도록 구현한다.
`String` 클래스의 `equals` 메소드 또한 위에서 설명한 형태로 오버라이딩되어 있기 때문에 실제 문자열의 내용을 비교한다.

실제로 `equals` 메소드가 참조가 아닌 속성으로 비교하는지 확인해보자.

```java
public class Main {

    public static void main(String[] args) {
        Person john1 = new Person("John", 30);
        Person john2 = new Person("John", 30);

        String name1 = john1.getName();
        String name2 = john2.getName();

        System.out.println(name1 == name2);
        System.out.println(name1.equals(name2));
    }
}
```

```shell
> Task :Main.main()
true
true
```

`equals` 메소드를 통해 속성을 비교하여 `true`를 반환하는 것을 볼 수 있다.

> 원래 `==` 연산자는 참조를 비교하기 때문에 당연히 `false`를 반환해야 한다.
> 하지만 Java의 문자열 리터럴, 즉 큰따옴표로 묶인 문자열(`"John"`과 같은)은 JVM의 특별한 영역인 String Pool에 저장된다.
> 동일한 문자열 리터럴을 사용하여 여러 개의 객체를 생성하면, JVM은 성능과 메모리 효율성을 위해 동일한 참조를 사용한다.
>
> - 동일한 문자열 리터럴을 사용하여 String 객체를 생성하면 동일한 참조를 갖게 된다.
> - 생성자를 통해 생성된 String 객체는 String Pool과는 독립적이므로 각각의 객체는 서로 다른 참조를 갖는다.
>
> ```java
> // 문자열 리터럴
> String name = "John";
> 
> // new String()
> String name = new String("John");
> ```
>
> ```java
> public class Main {
> 
>     public static void main(String[] args) {
>         String nickname1 = new String("nickname1");
>         String nickname2 = new String("nickname2");
> 
>         System.out.println(nickname1 == nickname2); // false 출력
>     }
> }
> ```