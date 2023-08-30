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
