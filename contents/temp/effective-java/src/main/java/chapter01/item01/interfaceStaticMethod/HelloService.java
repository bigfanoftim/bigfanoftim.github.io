package chapter01.item01.interfaceStaticMethod;


public interface HelloService {

    String hello();

    static String hi() {
        prepareMessage();
        return "hi";
    }

    static String hi2() {
        prepareMessage();
        return "hi";
    }

    static String hi3() {
        prepareMessage();
        return "hi";
    }

    static private void prepareMessage() {
    }

    default String bye() {
        return "bye";
    }
}

