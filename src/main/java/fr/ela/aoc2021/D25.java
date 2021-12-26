package fr.ela.aoc2021;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class D25 extends AoC {

    @Override
    public void run() {
        Grid grid = new Grid(list(getTestInputPath()));
        System.out.println("The test cucumbers stop moving after "+grid.stepUntilBlock()+" steps");
        grid = new Grid(list(getInputPath()));
        System.out.println("The real cucumbers stop moving after "+grid.stepUntilBlock()+" steps");
    }


    public record Position(int x, int y) {
    }

    public class Grid {
        Set<Position> southCucumbers;
        Set<Position> eastCucumbers;

        final int width;
        final int height;

        public Grid(List<String> list) {
            this.width = list.get(0).length();
            this.height = list.size();
            southCucumbers = new HashSet<>();
            eastCucumbers = new HashSet<>();
            for (int y = 0; y < list.size(); y++) {
                char[] chars = list.get(y).toCharArray();
                for (int x = 0; x < chars.length; x++) {
                    if (chars[x] == 'v') {
                        southCucumbers.add(new Position(x, y));
                    }
                    if (chars[x] == '>') {
                        eastCucumbers.add(new Position(x, y));
                    }
                }
            }
        }

        public int step() {
            int moves = 0;
            Set<Position> newEast = new HashSet<>();
            moves += move(eastCucumbers, newEast, this::east);
            this.eastCucumbers = newEast;
            Set<Position> newSouth = new HashSet<>();
            moves += move(southCucumbers, newSouth, this::south);
            this.southCucumbers = newSouth;
            return moves;
        }

        Position east(Position pos) {
            return new Position(pos.x == width - 1 ? 0 : pos.x + 1, pos.y);
        }

        Position south(Position pos) {
            return new Position(pos.x, pos.y == height - 1 ? 0 : pos.y + 1);
        }

        public boolean contains(Position pos) {
            return southCucumbers.contains(pos) || eastCucumbers.contains(pos);
        }

        private int move(Set<Position> origin, Set<Position> newPositions, Function<Position, Position> move) {
            int counter = 0;
            for (Position pos : origin) {
                Position newPos = move.apply(pos);
                if (contains(newPos)) {
                    newPositions.add(pos);
                } else {
                    newPositions.add(newPos);
                    counter++;
                }
            }
            return counter;
        }



        public int stepUntilBlock() {
            int steps = 0;
            int moves = -1;
            while (moves != 0) {
                moves = step();
                steps++;
            }
            return steps;
        }
    }

}
