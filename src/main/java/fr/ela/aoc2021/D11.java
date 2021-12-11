package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class D11 extends AoC {

    @Override
    public void run() {
        process(getTestInputPath(), "Test");
        process(getInputPath(), "Real");
    }

    public void process(Path input, String name) {
        Grid grid = new Grid(list(input));
        System.out.println("Grille " + name + " construite");
        System.out.println(grid);
        IntSummaryStatistics stats = IntStream.range(0, 100).map(i -> grid.flash())
                .summaryStatistics();

        System.out.println(name + " flashes : " +stats.getSum());
        if (stats.getMax() == grid.size()) {
            throw new IllegalStateException("Bummer");
        }
        int step = 101;
        while (grid.flash() < grid.size()) {
            step++;
        }
        System.out.println("All "+name+" octopusses flashes together at step "+step);
    }

    public class Grid {
        final int[][] grid;
        final int cols;
        final int rows;

        public Grid(List<String> input) {
            rows = input.size();
            cols = input.get(0).length();
            grid = new int[rows][cols];

            int row = 0;
            for (String line : input) {
                char[] chars = line.toCharArray();
                for (int col = 0; col < cols; col++) {
                    grid[row][col] = Character.getNumericValue(chars[col]);
                }
                row++;
            }
        }
        int size() {
            return cols*rows;
        }

        public Stream<Point> rowStream(final int row) {
            return IntStream.range(0, cols).mapToObj(col -> new Point(row, col));
        }

        public Stream<Point> pointStream() {
            return IntStream.range(0, rows).mapToObj(this::rowStream).flatMap(Function.identity());
        }

        public String rowToString(final int row) {
            return Arrays.stream(grid[row])
                    .mapToObj(Integer::toString)
                    .collect(Collectors.joining(""));
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("--------\n");
            sb.append(IntStream.range(0, rows).mapToObj(this::rowToString).collect(Collectors.joining("\n")));
            sb.append("\n-------");
            return sb.toString();
        }


        public int inc(Point p) {
            grid[p.row][p.col]++;
            return grid[p.row][p.col];
        }

        public void set(Point p, int value) {
            grid[p.row][p.col] = value;
        }

        public int get(Point p) {
            return grid[p.row][p.col];
        }

        int flash() {
            Set<Point> flashed = new HashSet<>();
            Stack<Point> flashing = new Stack<>();
            pointStream().filter(p -> inc(p) > 9).forEach(flashing::push);

            Point current;
            while (!flashing.isEmpty()) {
                current = flashing.pop();

                if (flashed.contains(current)) {
                    continue;
                }
                flashed.add(current);
                List<Point> adjacent = adjacent(current).collect(Collectors.toList());
                adjacent.stream().filter(p -> !flashed.contains(p))
                        .filter(p -> inc(p) > 9)
                        .forEach(flashing::push);

            }
            flashed.forEach(p -> set(p, 0));
            return flashed.size();
        }

        Stream<Point> adjacent(Point p) {
            return Stream.of(
                    getPoint(p, 1, 1),
                    getPoint(p, 1, 0),
                    getPoint(p, 1, -1),
                    getPoint(p, 0, 1),
                    getPoint(p, 0, -1),
                    getPoint(p, -1, 1),
                    getPoint(p, -1, 0),
                    getPoint(p, -1, -1))
                    .filter(Objects::nonNull);
        }

        Point getPoint(Point p, int dx, int dy) {
            int row = p.row + dx;
            int col = p.col + dy;
            if ((0 <= row && row < rows) && (0 <= col && col < cols)) {
                return new Point(row, col);
            } else {
                return null;
            }
        }

    }

    record Point(int row, int col) {

    }

}
