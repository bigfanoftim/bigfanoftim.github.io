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
