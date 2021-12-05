package fr.ela.aoc2021;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class D05 extends AoC {

    @Override
    public void run() {
        List<Line> testSpace = stream(getTestInputPath()).map(Line::parse).collect(Collectors.toList());
        System.out.println("Test result part 1 : " + findPositionsWithOverlappingLines(testSpace.stream().filter(Line::isAlongAxes), 2));
        System.out.println("Test result part 2 : " + findPositionsWithOverlappingLines(testSpace.stream(),2));

        List<Line> space = stream(getInputPath()).map(Line::parse).collect(Collectors.toList());
        System.out.println("Real result part 1 : " + findPositionsWithOverlappingLines(space.stream().filter(Line::isAlongAxes), 2));
        System.out.println("Real result part 2 : " + findPositionsWithOverlappingLines(space.stream(), 2));
    }

    record Position(int x, int y) {
        static Position parse(String s) {
            String[] pos = s.split(",");
            return new Position(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
        }
    }

    public static long findPositionsWithOverlappingLines(Stream<Line> linesStream, int number) {
        return linesStream
                .flatMap(Line::streamPositions)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() >= number)
                .count();
    }

    record Line(Position start, Position end, int length) {

        static Line parse(String s) {
            String[] l = s.split(" -> ");
            Position start = Position.parse(l[0]);
            Position end = Position.parse(l[1]);
            int length = Math.max(Math.abs(start.x - end.x), Math.abs(start.y - end.y));
            return new Line(start, end, length);
        }

        boolean isAlongAxes() {
            return start.x == end.x || start.y == end.y;
        }

        Stream<Position> streamPositions() {
            int incX = Integer.compare(end.x, start.x);
            int incY = Integer.compare(end.y, start.y);
            return IntStream.range(0, length + 1).mapToObj(n -> new Position(start.x + n * incX, start.y + n * incY));
        }
    }
}
