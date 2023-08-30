public class Main {
    public static void main(String[] args) {
        MyFunction f = new MyFunction() {
            public int max(int a, int b) {
                return a > b ? a : b;
            }
        };
        int max = f.max(5, 3);

        MyFunction f2 = (int a, int b) -> a > b ? a : b;
        int max2 = f2.max(10, 1);
    }
}
