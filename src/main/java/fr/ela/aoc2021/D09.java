package fr.ela.aoc2021;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class D09 extends AoC {
    @Override
    public void run() {
        Grid testGrid = new Grid(list(getTestInputPath()));
        List<Point> lowPointsTest = testGrid.getLowPoints();
        System.out.println("Resultat Test Part 1 : " + lowPointsTest.stream().mapToInt(lp -> lp.value + 1).sum());

        Grid grid = new Grid(list(getInputPath()));
        List<Point> lowPoints = grid.getLowPoints();
        System.out.println("Resultat Resl Part 1 : " + lowPoints.stream().mapToInt(lp -> lp.value + 1).sum());

    }

    public class Grid {
        final int[][] grid;
        final int rows;
        final int cols;

        public Grid(List<String> values) {
            rows = values.size();
            cols = values.get(0).length();
            grid = new int[rows][cols];
            for (int i = 0; i < rows; i++) {
                int[] row = new int[cols];
                char[] rowValues = values.get(i).toCharArray();
                for (int j = 0; j < cols; j++) {
                    row[j] = Character.getNumericValue(rowValues[j]);
                }
                grid[i] = row;
            }
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

        public int[] getRow(int row) {
            return grid[row];
        }

        public int getCol(int[] row, int col) {
            return row[col];
        }

        public int getValue(int row, int col) {
            return getCol(getRow(row), col);
        }

        public Optional<Point> lowPoint(int row, int col) {
            int pointValue = getValue(row, col);
            if (pointValue == 9) {
                return Optional.empty();
            }
            if (pointValue == 0) {
                return Optional.of(new Point(row, col, pointValue));
            }
            if (row > 0 && getValue(row -1, col) < pointValue) {
                return Optional.empty();
            }
            if (row +1 < rows && getValue(row + 1, col) < pointValue) {
                return Optional.empty();
            }
            int[] rowValue = getRow(row);
            if (col > 0 && getCol(rowValue, col - 1) < pointValue) {
                return Optional.empty();
            }
            if (col +1 < cols && getCol(rowValue, col+1) < pointValue) {
                return Optional.empty();
            }
            return Optional.of(new Point(row, col, pointValue));
        }

    }

    record Point(int row, int col, int value) {
    }

}
