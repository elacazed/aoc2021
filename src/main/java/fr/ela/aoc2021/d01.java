package fr.ela.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.stream.Collectors;

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
        return Files.lines(input).map(Integer::valueOf)
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
