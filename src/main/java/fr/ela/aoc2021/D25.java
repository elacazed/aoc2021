package fr.ela.aoc2021;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class D25 extends AoC {

    @Override
    public void run() {
        Grid grid = new Grid(list(getTestInputPath()));

        System.out.println(grid.stepUntilBlock());

        grid = new Grid(list(getInputPath()));

        System.out.println(grid.stepUntilBlock());

    }


    public record Position(int x, int y) {

        Position east(int width) {
            return new Position(x == width - 1 ? 0 : x + 1, y);
        }

        Position south(int height) {
            return new Position(x, y == height -1 ? 0 : y + 1);
        }
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
            for (Position pos : eastCucumbers) {
                Position move = pos.east(width);
                if (southCucumbers.contains(move) || eastCucumbers.contains(move)) {
                    newEast.add(pos);
                } else {
                    newEast.add(move);
                    moves++;
                }
            }
            this.eastCucumbers = newEast;
            Set<Position> newSouth  = new HashSet<>();
            for (Position pos : southCucumbers) {
                Position move = pos.south(height);
                if (southCucumbers.contains(move) || eastCucumbers.contains(move)) {
                    newSouth.add(pos);
                } else {
                    newSouth.add(move);
                    moves++;
                }
            }
            this.southCucumbers = newSouth;
            return moves;
        }


        public String toString() {
            char[][] chars = new char[height][width];
            for (int i = 0; i < height; i++) {
                char[] line = new char[width];
                Arrays.fill(line, '.');
                chars[i] = line;
            }
            for (Position p : southCucumbers) {
                chars[p.y][p.x] = 'v';
            }
            for (Position p : eastCucumbers) {
                chars[p.y][p.x] = '>';
            }
            return Arrays.stream(chars).map(String::new).collect(Collectors.joining("\n"));
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
