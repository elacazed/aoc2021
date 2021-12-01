package fr.ela.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class d01 {

    public static void main(String[] args) {
        try {
            List<Integer> testValues = Files.lines(InputFileUtil.getPath(d01.class, "input-test")).map(Integer::valueOf).collect(Collectors.toList());
            List<Integer> realValues = Files.lines(InputFileUtil.getPath(d01.class, "input")).map(Integer::valueOf).collect(Collectors.toList());
            System.out.println("Test file increases (simple) : " + findIncreases(testValues.toArray(new Integer[0]), 1));
            System.out.println("Real file increases (simple): " + findIncreases(realValues.toArray(new Integer[0]), 1));
            System.out.println("Test file increases : " + findIncreases(InputFileUtil.getPath(d01.class, "input-test"), 1));
            System.out.println("Real file increases : " + findIncreases(InputFileUtil.getPath(d01.class, "input"), 1));

            System.out.println("Test file increases window (simple) : " + findIncreases(testValues.toArray(new Integer[0]), 3));
            System.out.println("Real file increases window (simple) : " + findIncreases(realValues.toArray(new Integer[0]), 3));
            System.out.println("Test file increases window : " + findIncreases(InputFileUtil.getPath(d01.class, "input-test"), 3));
            System.out.println("Real file increases window : " + findIncreases(InputFileUtil.getPath(d01.class, "input"), 3));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int findIncreases(Path input, int windowSize) throws IOException {
        return Files.lines(input).map(Integer::valueOf)
                .collect(() -> new WindowCounter(windowSize), WindowCounter::add, (w, w1) -> {}).counter;
    }

    public static int findIncreases(Integer[] values, int windowSize) {
        int count = 0;
        for (int i = windowSize; i < values.length; i++) {
            int start = values[i - windowSize];
            if (values[i] > start) {
                count++;
            }
        }
        return count;
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
                if (value > window.pollLast()) {
                    counter++;
                }
            }
            window.push(value);
        }

    }
}
