package fr.ela.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.stream.Stream;

public class d01 {

    public static void main(String[] args) {
        try {
            System.out.println("Test file increases : " + findIncreases(InputFileUtil.getPath(d01.class, "input-test"), 1));
            System.out.println("Real file increases : " + findIncreases(InputFileUtil.getPath(d01.class, "input"), 1));

            System.out.println("Test file increases window : " + findIncreases(InputFileUtil.getPath(d01.class, "input-test"), 3));
            System.out.println("Real file increases window : " + findIncreases(InputFileUtil.getPath(d01.class, "input"), 3));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int findIncreases(Path input, int windowSize) throws IOException {
        Stream<String> measurements = Files.lines(input);
        return measurements.map(Integer::valueOf)
                .collect(() -> new WindowCounter(windowSize), WindowCounter::add, WindowCounter::merge).counter;
    }


    public static class WindowCounter {
        int counter = 0;
        LinkedList<Integer> window;
        final int windowSize;

        public WindowCounter(int windowSize) {
            this.windowSize = windowSize;
            this.counter = 0;
            this.window = new LinkedList<>();
        }

        public void add(int value) {
            if (window.size() == windowSize) {
                int last = window.pollLast();
                if (value > last) {
                    counter++;
                }
            }
            window.push(value);
        }

        public WindowCounter merge(WindowCounter other) {
            this.counter += other.counter;
            return this;
        }
    }


}
