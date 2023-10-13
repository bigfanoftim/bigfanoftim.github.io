# Predicate 활용

`Predicate` 인터페이스는 Java에서 하나의 인자를 받아 boolean 값을 반환하는 함수를 
나타내는 함수형 인터페이스이다. `java.util.function` 패키지에 정의되어 있으며, 주어진 인자를
평가하여 boolean 결과를 반환하는 `test` 메소드를 제공하는 것이 특징이다.

## 활용 예시

언제 `Predicate`를 활용할 수 있을지 예시를 통해 살펴보자.

```Java
public class Service {

    public boolean isEmailAvailable(String email) {
        return !isEmailInUse(email) && !isEmailBlocked(email);
    }
}
```

위 메소드는 이메일을 인자로 받아 해당 이메일이 사용할 수 있는 이메일인지 확인한다.
굉장히 단순하고 명확하여 검증 로직이 몇 개 되지 않는다면 직관적일 수 있다.

하지만 논리연산자를 사용하여 메소드를 구성하게 되면 조건마다 서로 독립적이지 않기 때문에 새로운 조건이 추가될 때마다
전체 조건문을 이해하고 적절한 위치에 조건을 추가해야 한다.

```Java
public class Service {

    public boolean isEmailAvailable(String email) {
        List<Predicate<String>> checks = Arrays.asList(
                providedEmail -> !isEmailInUse(providedEmail),
                providedEmail -> !isEmailBlocked(providedEmail)
        );

        return checks.stream().allMatch(check -> check.test(email));
    }
}
```

위와 같이 `Predicate`를 사용하면 각 조건이 독립적이며 새로운 조건을 추가하더라도 기존 코드에 영향을 주지 않는다.
