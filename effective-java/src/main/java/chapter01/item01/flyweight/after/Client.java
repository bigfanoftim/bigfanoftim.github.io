package chapter01.item01.flyweight.after;

public class Client {

    public static void main(String[] args) {
        FontFactory fontFactory = new FontFactory();

        // 같은 12pt 나눔체 폰트 객체를 사용하기 때문에 메모리를 좀 더 아낄 수 있다.
        Character c1 = new Character('a', "bigfanoftim", fontFactory.getFont("nanum:12"));
        Character c2 = new Character('b', "bigfanoftim", fontFactory.getFont("nanum:12"));
        Character c3 = new Character('c', "bigfanoftim", fontFactory.getFont("nanum:12"));
    }
}
