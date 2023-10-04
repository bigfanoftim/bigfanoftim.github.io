public class Computer {

    private String CPU;
    private String RAM;
    private String storage;

    private Computer(Builder builder) {
        this.CPU = builder.CPU;
        this.RAM = builder.RAM;
        this.storage = builder.storage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String CPU;
        private String RAM;
        private String storage;

        public Builder withCPU(String CPU) {
            this.CPU = CPU;
            return this;
        }

        public Builder withRAM(String RAM) {
            this.RAM = RAM;
            return this;
        }

        public Builder withStorage(String storage) {
            this.storage = storage;
            return this;
        }

        public Computer build() {
            return new Computer(this);
        }
    }
}
