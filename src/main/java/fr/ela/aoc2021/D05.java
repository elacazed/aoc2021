package fr.ela.aoc2021;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class D05 extends AoC {


    @Override
    public void run() {
        Space testSpace = stream(getTestInputPath()).map(Line::parse).filter(Objects::nonNull).collect(Collectors.toCollection(Space::new));
        System.out.println("Test result part 1 : " + testSpace.findPositionsWithOverlappingLines(2, l -> EnumSet.of(Direction.HORIZONTAL, Direction.VERTICAL).contains(l.direction)));
        System.out.println("Test result part 2 : " + testSpace.findPositionsWithOverlappingLines(2, l -> true));

        Space space = stream(getInputPath()).map(Line::parse).filter(Objects::nonNull).collect(Collectors.toCollection(Space::new));
        System.out.println("Real result part 1 : " + space.findPositionsWithOverlappingLines(2, l -> EnumSet.of(Direction.HORIZONTAL, Direction.VERTICAL).contains(l.direction)));
        System.out.println("Real result part 2 : "+space.findPositionsWithOverlappingLines(2, l -> true));
    }

    record Position(int x, int y) {
        static Position parse(String s) {
            String[] pos = s.split(",");
            return new Position(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public static class Space extends ArrayList<Line> {

        public long findPositionsWithOverlappingLines(int number, Predicate<Line> filter) {
            return stream().filter(filter)
                    .map(Line::positions)
                    .flatMap(List::stream)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet().stream()
                    .filter(e -> e.getValue() >= number)
                    .count();
        }
    }

    private enum Direction {
        HORIZONTAL,
        VERTICAL,
        DIAGONAL;

        static Direction get(Position start, Position end) {
            if (Math.abs(start.y - end.y) == Math.abs(start.x - end.x)) {
                return Direction.DIAGONAL;
            }
            if (start.x == end.x) {
                return Direction.VERTICAL;
            }
            if (start.y == end.y) {
                return Direction.HORIZONTAL;
            }
            return null;
        }
    }

    record Line(Position start, Position end, Direction direction, int length) {

        static Line parse(String s) {
            String[] l = s.split(" -> ");
            Position start = Position.parse(l[0]);
            Position end = Position.parse(l[1]);
            Direction dir = Direction.get(start, end);
            int length = Math.max(Math.abs(start.x - end.x), Math.abs(start.y - end.y));
            if (dir == null) {
                return null;
            } else {
                return new Line(start, end, dir, length);
            }
        }

        int getInc(int start, int end) {
            if (start == end) {
                return 0;
            }
            return start < end ? 1 : -1;
        }

        List<Position> positions() {
            List<Position> positions = new ArrayList<>();
            int incX = getInc(start.x, end.x);
            int incY = getInc(start.y, end.y);
            for (int n = 0; n <= length; n++) {
                positions.add(new Position(start.x + n * incX, start.y + n * incY));
            }
            return positions;
        }

    }
}
