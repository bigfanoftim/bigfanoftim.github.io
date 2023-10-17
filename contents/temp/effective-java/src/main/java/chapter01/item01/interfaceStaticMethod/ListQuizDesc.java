package chapter01.item01.interfaceStaticMethod;

import java.util.ArrayList;
import java.util.Comparator;

public class ListQuizDesc {

    public static void main(String[] args) {
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(100);
        numbers.add(20);
        numbers.add(44);
        numbers.add(3);

        System.out.println("numbers = " + numbers);

        /**
         * Comparator<Integer> desc = new Comparator<Integer>() {
         *     @Override
         *     public int compare(Integer o1, Integer o2) {
         *         return o2 - o1;
         *     }
         * };
         */
        Comparator<Integer> desc = (o1, o2) -> o2 - o1; // 위의 코드 lambda 식으로 수정

        /**
         * Collections.sort(numbers, desc);
         */
        numbers.sort(desc); // Collections의 sort를 사용하거나 List의 sort 사용
        System.out.println("desc = " + numbers);
    }
}
