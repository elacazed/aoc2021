package fr.ela.aoc2021;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class D01 extends AoC {

    public void run() {
        System.out.println("Test file increases : " + findIncreases(stream(getTestInputPath(), Integer::valueOf), 1));
        System.out.println("Real file increases : " + findIncreases(stream(getInputPath(), Integer::valueOf), 1));

        System.out.println("Test file increases window : " + findIncreases(stream(getTestInputPath(), Integer::valueOf), 3));
        System.out.println("Real file increases window : " + findIncreases(stream(getInputPath(), Integer::valueOf), 3));
    }

    public static int findIncreases(Stream<Integer> input, int windowSize) {
        return input
                .collect(Collectors.toCollection(() -> new Counter(windowSize))).counter;
    }

    public static class Counter extends LinkedList<Integer> {
        final int size;
        int counter = 0;

        public Counter(int size) {
            this.size = size;
        }

        @Override
        public boolean add(Integer integer) {
            if (size() == size && integer > pollLast()) {
                counter++;
            }
            push(integer);
            return true;
        }

    }
}
