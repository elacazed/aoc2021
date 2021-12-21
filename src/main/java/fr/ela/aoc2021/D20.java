package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class D20 extends AoC {


    @Override
    public void run() {
        resolve(getTestInputPath(), "Test");
        resolve(getInputPath(), "Real");
    }


    public void resolve(Path path, String name) {
        List<String> lines = list(path);
        int[] map = lines.get(0).chars().map(c -> ((char) c == '#' ? 1 : 0)).toArray();

        int max = lines.size();

        Grid grid = new Grid(max);
        for (int i = 2; i < lines.size(); i++) {
            char[] chars = lines.get(i).toCharArray();
            for (int j = 0; j < chars.length; j++) {
                if (chars[j] == '#') {
                    grid.add(new Position(j, i - 2));
                }
            }
        }

        Grid g = grid.enhance(map).enhance(map);
        System.out.println(name + " map : " + g.lightPoints() + " lightpoints after 2 enhancements");

        long time = System.nanoTime();
        for (int i = 0; i < 50; i++) {
            grid = grid.enhance(map);
        }
        System.out.println(name + " map : " + grid.lightPoints() + " lightpoints after 50 enhancements");
        System.out.println("Time : " + ((System.nanoTime() - time) / 1000000) + " milliseconds");

    }


    public record Position(int x, int y) {

        static Position of(int x, int y) {
            return new Position(x, y);
        }

        List<Position> neighbours() {
            return List.of(Position.of(x - 1, y - 1),
                    Position.of(x, y - 1),
                    Position.of(x + 1, y - 1),
                    Position.of(x - 1, y),
                    this,
                    Position.of(x + 1, y),
                    Position.of(x - 1, y + 1),
                    Position.of(x, y + 1),
                    Position.of(x + 1, y + 1));
        }


    }

    public class Grid {

        final int minX;
        final int maxX;
        final int minY;
        final int maxY;

        final int outsideValue;

        Set<Position> lightPoints = new HashSet<>();

        public Grid(int max) {
            this(0, 0, max, 0, max);
        }

        public Grid(int outsideValue, int minX, int maxX, int minY, int maxY) {
            this.outsideValue = outsideValue;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        public int lightPoints() {
            return lightPoints.size();
        }

        public Grid enhance(int[] line) {
            int outsideValueEnhanced = this.outsideValue == 0 ? line[0] : line[511];
            Grid g = new Grid(outsideValueEnhanced, minX - 1, maxX + 1, minY - 1, maxY + 1);

            for (int x = g.minX; x <= g.maxX; x++) {
                for (int y = g.minY - 1; y <= g.maxY; y++) {
                    g.add(enhance(Position.of(x, y), line));
                }
            }
            return g;
        }

        private void add(Position position) {
            if (position != null) {
                lightPoints.add(position);
            }
        }

        boolean inside(Position p) {
            return p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY;
        }

        int valueOf(Position p) {
            if (inside(p)) {
                return lightPoints.contains(p) ? 1 : 0;
            } else {
                return outsideValue == 1 ? 1 : 0;
            }
        }

        public Position enhance(Position p, int[] line) {
            List<Position> neigbours = p.neighbours();

            int index = valueOf(neigbours.get(8));
            index += valueOf(neigbours.get(7)) * 2;
            index += valueOf(neigbours.get(6)) * 4;
            index += valueOf(neigbours.get(5)) * 8;
            index += valueOf(neigbours.get(4)) * 16;
            index += valueOf(neigbours.get(3)) * 32;
            index += valueOf(neigbours.get(2)) * 64;
            index += valueOf(neigbours.get(1)) * 128;
            index += valueOf(neigbours.get(0)) * 256;
            if (line[index] == 1) {
                return new Position(p.x, p.y);
            }
            return null;
        }


    }

}

