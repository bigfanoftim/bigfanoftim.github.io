# 스트림(stream)

Collection이나 Iterator와 같은 인터페이스를 이용해서 컬렉션을 다루는 방식을 표준화하기는 했으나, 각 컬렉션 클래스에는 같은 기능의 메소드들이 중복해서 정의되어 있다.

### 스트림없이 정렬
예를 들어 List를 정렬할 때는 `Collections.sort()`를 사용하고

```java
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 6, 8, 0);
        Collections.sort(numbers);

        for (Integer number : numbers) {
            System.out.println("number = " + number);
        }
    }
}
```

```sh
# 출력 결과
number = 0
number = 1
number = 3
number = 4
number = 6
number = 8
```

배열을 정렬할 때는 `Arrays.sort()`를 사용한다.

```java
import java.util.Arrays;

public class ArrayExample {
    public static void main(String[] args) {
        int[] numbers = {3, 2, 1, 6, 7, 4};
        Arrays.sort(numbers);

        for (int number : numbers) {
            System.out.println("number = " + number);
        }
    }
}
```

```sh
# 출력 결과
number = 1
number = 2
number = 3
number = 4
number = 6
number = 7
```

### 스트림 사용하여 정렬

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 6, 8, 0);

        List<Integer> list = numbers.stream().sorted().collect(Collectors.toList());
        for (Integer number : list) {
            System.out.println("number = " + number);
        }
    }
}
```

```java
import java.util.Arrays;

public class ArrayExample {
    public static void main(String[] args) {
        int[] numbers = {3, 2, 1, 6, 7, 4};

        int[] sorted = Arrays.stream(numbers).sorted().toArray();

        for (int number : sorted) {
            System.out.println("number = " + number);
        }
    }
}
```

스트림은 데이터 소스를 추상화하고, 데이터를 다루는데 자주 사용되는 메소드들을 정의해 놓았다. 데이터 소스를 추상화하였다는 것은, 어떤 데이터 소스이던 간에 같은 방식으로 다룰 수 있게 되는 것. 코드의 재사용성이 높아진다.