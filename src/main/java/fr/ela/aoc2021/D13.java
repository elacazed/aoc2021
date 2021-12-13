package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class D13 extends AoC {

    private static final Pattern FOLD_PATTERN = Pattern.compile("fold along ([xy])=([0-9]+)");

    @Override
    public void run() {
        process(getTestInputPath(), "Test");
        process(getInputPath(), "Real");
    }

    public void process(Path input, String name) {
        Map<Boolean, List<String>> linesSorted = stream(input)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.groupingBy(l -> l.charAt(0) == 'f'));

        Grid grid = new Grid();
        linesSorted.get(Boolean.FALSE).stream()
                .map(Point::fromLine).forEach(grid::addPoint);
        List<FoldingInstruction> folds = linesSorted.get(Boolean.TRUE).stream()
                .map(FoldingInstruction::fromLine).collect(Collectors.toList());

        grid.fold(folds.get(0));

        System.out.println(name + " grid has " + grid.visibleDots() + " visible dots after first fold");

        folds.stream().skip(1).forEach(grid::fold);

        System.out.println(name + " Grid after all folding instructions : ");
        System.out.println(grid);

    }


    public class Grid {

        Set<Point> points = new HashSet<>();

        int nbCols;
        int nbRows;

        Grid() {
        }

        public void addPoint(Point p) {
            nbCols = Math.max(p.col, nbCols);
            nbRows = Math.max(p.row, nbRows);
            points.add(p);
        }

        public void fold(FoldingInstruction fold) {
            List<Point> toRemove = points.stream().filter(fold::moves).collect(Collectors.toList());
            toRemove.forEach(points::remove);
            toRemove.stream().map(fold::fold).forEach(this::addPoint);
            fold.resize(this);
        }

        public String toString() {
            int maxCol = nbCols + 1;
            int maxRow = nbRows + 1;

            List<char[]> s = new ArrayList<>();
            for (int row = 0; row < maxRow + 1; row++) {
                char[] chars = new char[maxCol + 1];
                Arrays.fill(chars, '.');
                s.add(chars);
            }
            for (Point p : points) {
                s.get(p.row)[p.col] = '#';
            }
            StringBuilder sb = new StringBuilder("----------\n");
            for (int i = 0; i < s.size(); i++) {
                sb.append(i).append("\t").append(new String(s.get(i))).append("\n");
            }
            return sb.toString();
        }


        public int visibleDots() {
            return points.size();
        }
    }

    public enum Direction {
        X((g, value) -> { g.nbCols = (g.nbCols -1) /2;},
                (p, value) -> p.col > value,
                (p, value) -> new Point(p.row, 2 * value - p.col)),
        Y((g, value) -> { g.nbRows = (g.nbRows -1) /2;},
                (p, value) -> p.row > value,
                (p, value) -> new Point(2 * value - p.row, p.col));

        final BiConsumer<Grid, Integer> resize;
        final BiFunction<Point, Integer, Point> foldAlong;
        final BiPredicate<Point, Integer> folds;

        Direction(BiConsumer<Grid, Integer> resize, BiPredicate<Point, Integer> folds, BiFunction<Point, Integer, Point> foldAlong) {
            this.resize = resize;
            this.folds = folds;
            this.foldAlong = foldAlong;
        }
    }

    public record FoldingInstruction(Direction axis, int value) {
        public static FoldingInstruction fromLine(String line) {
            Matcher matcher = FOLD_PATTERN.matcher(line);
            if (matcher.matches()) {
                Direction d = matcher.group(1).charAt(0) == 'x' ? Direction.X : Direction.Y;
                return new FoldingInstruction(d, Integer.parseInt(matcher.group(2)));
            }
            throw new IllegalArgumentException(line);
        }

        public boolean moves(Point p) {
            return axis.folds.test(p, value);
        }

        public Point fold(Point p) {
            return axis.foldAlong.apply(p, value);
        }

        public void resize(Grid grid) {
            axis.resize.accept(grid, value);
        }
    }

    record Point(int row, int col) {
        public static Point fromLine(String line) {
            String[] coords = line.split(",");
            return new Point(Integer.parseInt(coords[1]), Integer.parseInt(coords[0]));
        }
    }

}
