public class Builder {

    public static void main(String[] args) {
        Computer computer = Computer.builder()
                .withCPU("Intel")
                .withRAM("32GB")
                .withStorage("2TB SSD")
                .build();
    }
}
