package chapter01.item01.interfaceStaticMethod;

import java.util.ArrayList;
import java.util.Comparator;

public class ListQuizAsc {

    public static void main(String[] args) {
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(100);
        numbers.add(20);
        numbers.add(44);
        numbers.add(3);

        System.out.println("numbers = " + numbers);

        Comparator<Integer> desc = (o1, o2) -> o2 - o1;
        numbers.sort(desc.reversed());

        System.out.println("asc = " + numbers);
    }
}
