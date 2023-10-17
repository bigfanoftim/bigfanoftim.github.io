package chapter01.item01;

import java.util.Optional;
import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        ServiceLoader<HelloService> load = ServiceLoader.load(HelloService.class);
        Optional<HelloService> helloServiceOptional = load.findFirst();
        helloServiceOptional.ifPresent(helloService -> {
            System.out.println(helloService.hello());
        });
    }
}
