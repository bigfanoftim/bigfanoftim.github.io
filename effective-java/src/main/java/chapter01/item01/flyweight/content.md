# 플라이웨이트(Flyweight) 패턴

### 참고
- [YES24 - 이펙티브 자바 Effective Java 3/E](https://www.yes24.com/Product/Goods/65551284)
- [인프런 - 이펙티브 자바 완벽 공략 1부](https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#curriculum)


플라이웨이트 패턴은 객체를 가볍게 만들어 메모리 사용을 줄이는 패턴이다.
자주 사용하는 속성(또는 외적인 속성, extrinsic)과 변하지 않는 속성(또는 내적인 속성, intrinsic)을 분리하고 재사용하여 메모리 사용을 줄일 수 있다.

## 예시

```java
public class Character {

    private char value;

    private String color;

    private String fontFamily;

    private int fontSize;

    public Character(char value, String color, String fontFamily, int fontSize) {
        this.value = value;
        this.color = color;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
    }
}
```

위와 같이 `Character`라는 클래스가 있다고 가정하자. 이때 `fontFamily`, `fontSize`는 자주 묶어서 사용되는 값이다.
모든 캐릭터마다 이러한 값을 갖고 있게 되면 불필요하게 무겁다. 따라서 아래와 같이 개선해 볼 수 있다.

```java
public class Character {

    private char value;

    private String color;

    private Font font;

    public Character(char value, String color, Font font) {
        this.value = value;
        this.color = color;
        this.font = font;
    }
}
```

```java
public class Font {

    final String family;

    final int size;

    public Font(String family, int size) {
        this.family = family;
        this.size = size;
    }

    public String getFamily() {
        return family;
    }

    public int getSize() {
        return size;
    }
}
```

```java
public class FontFactory {

    private Map<String, Font> cache = new HashMap<>();

    public Font getFont(String font) {
        if (cache.containsKey(font)) {
            return cache.get(font);
        } else {
            String[] split = font.split(":");
            Font newFont = new Font(split[0], Integer.parseInt(split[1]));
            cache.put(font, newFont);
            return newFont;
        }
    }
}
```

```java
public class Client {

    public static void main(String[] args) {
        FontFactory fontFactory = new FontFactory();

        // 같은 12pt 나눔체 폰트 객체를 사용하기 때문에 메모리를 좀 더 아낄 수 있다.
        Character c1 = new Character('a', "bigfanoftim", fontFactory.getFont("nanum:12"));
        Character c2 = new Character('b', "bigfanoftim", fontFactory.getFont("nanum:12"));
        Character c3 = new Character('c', "bigfanoftim", fontFactory.getFont("nanum:12"));
    }
}
```