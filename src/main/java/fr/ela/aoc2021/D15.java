package fr.ela.aoc2021;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D15 extends AoC {


    @Override
    public void run() {
        findSafestPath(getTestInputPath(), "Test");
        findSafestPath(getInputPath(), "Real");
    }

    public void findSafestPath(Path input, String name) {
        List<int[]> lines = list(input, s -> s.chars().map(Character::getNumericValue).toArray());
        walk(name, new Grid(lines));
        walk(name, grow(lines));
    }

    private void walk(String name, Grid large) {
        long nanos = System.nanoTime();
        int result = large.safestPathRiskLevel(large.start, large.end);
        nanos = System.nanoTime() - nanos;
        System.out.println(name + " Safe path cost : " + result + " [" + nanos / 1000000 + " millis]");
    }


    public class Grid {

        final Map<Point, Integer> points;
        final int nbRows;
        final int nbCols;

        final Point start;
        final Point end;

        public Grid(List<int[]> lines) {
            points = new HashMap<>();
            nbRows = lines.size();
            nbCols = lines.get(0).length;
            IntStream.range(0, nbRows)
                    .mapToObj(row -> IntStream.range(0, nbCols).mapToObj(col -> new Point(row, col)))
                    .flatMap(Function.identity())
                    .forEach(p -> points.put(p, lines.get(p.row)[p.col]));
            start = new Point(0, 0);
            end = new Point(nbRows - 1, nbCols - 1);
        }

        public int safestPathRiskLevel(Point from, Point to) {
            Queue<Edge> queue = new PriorityQueue<>();
            Set<Point> path = new HashSet<>();
            queue.add(new Edge(from, 0));
            while (!queue.isEmpty() && !queue.peek().at().equals(to)) {
                var top = queue.poll();
                top.at().adjecents().stream().filter(this::inGrid).
                        filter(p -> !path.contains(p))
                        .map(p -> new Edge(p, top.cost() + points.get(p)))
                        .forEach(edge -> {
                            path.add(edge.at());
                            queue.add(edge);
                        });
            }
            return queue.peek().cost();
        }

        public boolean inGrid(Point p) {
            return 0 <= p.row && 0 <= p.col && nbRows > p.row && nbCols > p.col;
        }
    }

    private Grid grow(List<int[]> lines) {
        List<int[]> newLines = new ArrayList<>();
        lines.stream().map(this::growLine).collect(Collectors.toCollection(() -> newLines));
        int nbLines = lines.size();
        IntStream.range(nbLines, nbLines * 5)
                .mapToObj(i -> increase(newLines.get(i - nbLines), 1))
                .collect(Collectors.toCollection(() -> newLines));
        return new Grid(newLines);
    }

    private int[] growLine(int[] line) {
        int[] newLine = new int[line.length * 5];
        for (int time = 0; time < 5; time++) {
            System.arraycopy(increase(line, time), 0, newLine, line.length * time, line.length);
        }
        return newLine;
    }

    private int[] increase(int[] input, int inc) {
        return IntStream.of(input).map(v -> v + inc).map(v -> v > 9 ? v - 9 : v).toArray();
    }

    private static record Point(int row, int col) {

        List<Point> adjecents() {
            return List.of(new Point(row - 1, col), new Point(row + 1, col), new Point(row, col - 1), new Point(row, col + 1));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return row == point.row && col == point.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    private static record Edge(Point at, int cost) implements Comparable<Edge> {
        private static final Comparator<Edge> RISK_COMPARATOR = Comparator.comparingInt(Edge::cost);

        @Override
        public int compareTo(Edge o) {
            return RISK_COMPARATOR.compare(this, o);
        }
    }
}
