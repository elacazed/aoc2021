package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D13 extends AoC {


    @Override
    public void run() {
        process(getTestInputPath(), "Test");
        process(getInputPath(), "Real");
    }

    public void process(Path input, String name) {

        Map<Boolean, List<String>> linesSorted = stream(input)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.groupingBy(l -> l.charAt(0) == 'f'));

        Set<Point> grid = linesSorted.get(Boolean.FALSE).stream()
                .map(s -> s.split(","))
                .map(s -> new Point(Integer.parseInt(s[1]), Integer.parseInt(s[0])))
                .collect(Collectors.toSet());

        List<Function<Point, Point>> folds = linesSorted.get(Boolean.TRUE).stream()
                .map(this::toFold)
                .collect(Collectors.toList());

        grid = fold(grid, folds.get(0));
        System.out.println(name + " grid has " + grid.size() + " visible dots after first fold");
        grid = fold(grid, folds.stream().skip(1).reduce(Function::andThen).orElseThrow());
        System.out.println(name + " Grid after all folding instructions : ");
        System.out.println(toString(grid));
    }

    public Set<Point> fold(Set<Point> set, Function<Point, Point> folder) {
        return set.stream().map(folder).collect(Collectors.toSet());
    }

    public Function<Point, Point> toFold(String value) {
        String[] values = value.split("=");
        int axisPos = Integer.parseInt(values[1]);
        if (values[0].endsWith("x")) {
            return p -> p.col > axisPos ? new Point(p.row, (2 * axisPos) - p.col) : p;
        } else {
            return p -> p.row > axisPos ? new Point((2 * axisPos) - p.row, p.col) : p;
        }
    }

    public char[] line(char c, int size) {
        char[] chars = new char[size];
        Arrays.fill(chars, c);
        return chars;
    }

    public String toString(Set<Point> set) {
        Point max = set.stream().reduce(Point::maxPoint).orElseThrow();
        List<char[]> s = IntStream.range(0, max.row + 1).mapToObj(i -> line(' ', max.col + 1)).collect(Collectors.toList());
        set.forEach(p -> s.get(p.row)[p.col] = '#');
        return s.stream().map(String::new).collect(Collectors.joining("\n"));
    }

    record Point(int row, int col) {
        Point maxPoint(Point other) {
            return new Point(Math.max(row, other.row), Math.max(col, other.col));
        }
    }
}
