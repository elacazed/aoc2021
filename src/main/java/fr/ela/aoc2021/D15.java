package fr.ela.aoc2021;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class D15 extends AoC {


    @Override
    public void run() {
        findSafestPath(getTestInputPath(), "Test");
        findSafestPath(getInputPath(), "Real");
    }

    public void findSafestPath(Path input, String name) {
        List<int[]> lines = list(input, s -> s.chars().map(Character::getNumericValue).toArray());
        Grid grid = new Grid(lines);
        System.out.println(name + " Safe path cost : " + grid.safestPathRiskLevel(grid.start, grid.end));

        Grid large = grow(lines);
        System.out.println(name + " Safe path cost : " + large.safestPathRiskLevel(large.start, large.end));
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
            for (int row = 0; row < nbRows; row++) {
                for (int col = 0; col < nbCols; col++) {
                    points.put(new Point(row, col), lines.get(row)[col]);
                }
            }
            start = new Point(0, 0);
            end = new Point(nbRows - 1, nbCols - 1);
        }

        public int safestPathRiskLevel(Point from, Point to) {
            Queue<Edge> queue = new PriorityQueue<>();
            Set<Point> path = new HashSet<>();
            queue.add(new Edge(from, 0));
            while (!queue.isEmpty() && !queue.peek().at().equals(to)) {
                var top = queue.poll();
                top.at().adjecents().stream().filter(points::containsKey).filter(c -> !path.contains(c))
                        .map(c -> new Edge(c, top.cost() + points.get(c))).forEach(s -> {
                    path.add(s.at());
                    queue.add(s);
                });

            }
            return queue.peek().cost();
        }

    }


    private Grid grow(List<int[]> lines) {
        List<int[]> newLines = new ArrayList<>();
        int length = lines.get(0).length;
        for (int[] line : lines) {
            int[] newLine = new int[line.length * 5];
            System.arraycopy(line, 0, newLine, 0, line.length);
            for (int time = 1; time < 5; time++) {
                System.arraycopy(increase(line, time), 0,newLine,length * time, line.length);
            }
            newLines.add(newLine);
        }
        int nbLines = lines.size();
        for (int i = nbLines; i < nbLines*5; i++) {
            newLines.add(increase(newLines.get(i - nbLines), 1));
        }
        return new Grid(newLines);
    }

    private int[] increase(int[] input, int inc) {
        int[] out = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            int val = input[i] + inc;
            out[i] = val > 9 ? val -9 : val;
        }
        return out;
    }

    private static record Point(int row, int col) {
        List<Point> adjecents() {
            return List.of(new Point(row - 1, col), new Point(row + 1, col), new Point(row, col - 1), new Point(row, col + 1));
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
