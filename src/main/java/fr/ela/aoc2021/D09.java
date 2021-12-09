package fr.ela.aoc2021;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class D09 extends AoC {
    @Override
    public void run() {
        Grid testGrid = new Grid(list(getTestInputPath()));
        List<Point> lowPointsTest = testGrid.getLowPoints();
        System.out.println("Resultat Test Part 1 : " + lowPointsTest.stream().mapToInt(lp -> lp.value + 1).sum());

        int value = lowPointsTest.stream().map(testGrid::getBassin)
                .mapToInt(Set::size)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .reduce(1, (x,y) -> x*y);
        System.out.println("Résultat test PArt 2 : "+value);


        Grid grid = new Grid(list(getInputPath()));
        List<Point> lowPoints = grid.getLowPoints();
        System.out.println("Resultat Reel Part 1 : " + lowPoints.stream().mapToInt(lp -> lp.value + 1).sum());
        value = lowPoints.stream().map(grid::getBassin)
                .mapToInt(Set::size)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .reduce(1, (x,y) -> x*y);
        System.out.println("Résultat reel Part 2 : "+value);

    }


    public class Grid {
        final List<String> grid;
        final int rows;
        final int cols;

        public Grid(List<String> values) {
            rows = values.size();
            cols = values.get(0).length();
            grid = values;
        }

        public List<Point> getLowPoints() {
            List<Point> points = new ArrayList<>();
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    lowPoint(row, col).ifPresent(points::add);
                }
            }
            return points;
        }

        public String getRow(int row) {
            return grid.get(row);
        }

        public int getCol(String row, int col) {
            return Character.getNumericValue(row.charAt(col));
        }

        public int getValue(int row, int col) {
            return getCol(getRow(row), col);
        }

        public Optional<Point> lowPoint(int row, int col) {
            int pointValue = getValue(row, col);
            Point point = new Point(row, col, pointValue);
            if (pointValue == 9) {
                return Optional.empty();
            }
            if (pointValue == 0) {
                return Optional.of(point);
            }
            if (point.adjacent(this).allMatch(p -> p.value > pointValue)) {
                return Optional.of(point);
            }
            return Optional.empty();
        }

        Point inGrid(Point p) {
            if (p.row >= 0 && p.row < rows && p.col >= 0 && p.col < cols) {
                return new Point(p.row, p.col, getValue(p.row, p.col));
            } else {
                return null;
            }
        }

        public Set<Point> getBassin(Point low) {
            return getBassin(low, new HashSet<>());
        }


        public Set<Point> getBassin(Point low, Set<Point> bassin) {
            bassin.add(low);
            for (Point adjacent : low.adjacent(this).filter(p -> p.value < 9).filter(p -> !bassin.contains(p)).collect(Collectors.toList())) {
                bassin.addAll(getBassin(adjacent, bassin));
            }
            return bassin;
        }


    }

    record Point(int row, int col, int value) {

        Point(int row, int col) {
            this(row, col, -1);
        }

        Stream<Point> adjacent(Grid grid) {
            return Stream.of(new Point(row - 1, col), new Point(row, col - 1), new Point(row, col + 1), new Point(row + 1, col))
                    .map(grid::inGrid)
                    .filter(Objects::nonNull);
        }

        public String toString() {
            return "[" + row + "," + col + "] (" + value + ")";
        }
    }

}
