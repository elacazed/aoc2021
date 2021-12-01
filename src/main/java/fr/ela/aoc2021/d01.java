package fr.ela.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class d01 {

    public static void main(String[] args) {
        try {
            System.out.println("Test file increases : " + findIncreases(InputFileUtil.getPath(d01.class, "input-test")));
            System.out.println("Real file increases : " + findIncreases(InputFileUtil.getPath(d01.class, "input")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int findIncreases(Path input) throws IOException {
        Stream<String> measurements = Files.lines(input);
        return measurements.map(Integer::valueOf)
                .collect(new CountingIncreasesCollector());
    }





    public static class Counter {
        int counter = 0;
        int last = -1;

        public Counter() {
            this.counter = 0;
        }

        public void add(int value) {
            if (last != -1 && value > last) {
                counter++;
            }
            last = value;
        }

        public Counter add(Counter other) {
            this.counter += other.counter;
            return this;
        }
    }

    public static class CountingIncreasesCollector implements Collector<Integer, Counter, Integer> {

        @Override
        public Supplier<Counter> supplier() {
            return Counter::new;
        }

        @Override
        public BiConsumer<Counter, Integer> accumulator() {
            return Counter::add;
        }

        @Override
        public BinaryOperator<Counter> combiner() {
            return Counter::add;
        }

        @Override
        public Function<Counter, Integer> finisher() {
            return c -> c.counter;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

}
