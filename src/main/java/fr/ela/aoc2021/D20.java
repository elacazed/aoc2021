package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class D20 extends AoC {


    @Override
    public void run() {
        resolve(getTestInputPath(), "Test");
        resolve(getInputPath(), "Real");
    }


    public void resolve(Path path, String name) {
        List<String> lines = list(path);
        String map = lines.get(0);

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

        Grid g = grid.enhance(map, '0').enhance(map, map.charAt(0) == '.' ? '0':'1');
        System.out.println(name + " map : " + g.lightPoints() + " lightpoints after 2 enhancements");
        for (int i = 0; i < 50; i++) {
            grid = grid.enhance(map, (i % 2 == 0) ? '0' : (map.charAt(0) == '.' ? '0':'1'));
        }
        System.out.println(name + " map : " + grid.lightPoints() + " lightpoints after 50 enhancements");

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


        Set<Position> lightPoints = new HashSet<>();

        public Grid(int max) {
            this(0, max, 0, max);
        }

        public Grid(int minX, int maxX, int minY, int maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        public int lightPoints() {
            return lightPoints.size();
        }

        public Grid enhance(String line, char def) {
            Grid g = new Grid(minX - 1, maxX + 1, minY - 1, maxY + 1);

            for (int x = g.minX; x <= g.maxX; x++) {
                for (int y = g.minY - 1; y <= g.maxY; y++) {
                    enhance(Position.of(x, y), line, def).ifPresent(g::add);
                }
            }
            return g;
        }

        private void add(Position position) {
            lightPoints.add(position);
        }

        boolean inside(Position p) {
            return p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY;
        }

        char valueOf(Position p, char def) {
            if (inside(p)) {
                return lightPoints.contains(p) ? '1' : '0';
            } else {
                return def;
            }
        }

        public Optional<Position> enhance(Position p, String line, char def) {
            char[] chars = new char[9];
            List<Position> neigbours = p.neighbours();
            for (int i = 0; i < 9; i++) {
                chars[i] = valueOf(neigbours.get(i), def);
            }
            int index = Integer.parseInt(new String(chars), 2);
            if (line.charAt(index) == '#') {
                return Optional.of(new Position(p.x, p.y));
            }
            return Optional.empty();
        }

        public String toString() {
            IntSummaryStatistics xStats = lightPoints.stream().mapToInt(p -> p.x).summaryStatistics();
            IntSummaryStatistics yStats = lightPoints.stream().mapToInt(p -> p.y).summaryStatistics();

            StringBuilder sb = new StringBuilder();
            for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
                for (int x = xStats.getMin(); x <= xStats.getMax(); x++) {
                    sb.append(lightPoints.contains(Position.of(x, y)) ? '#' : '.');
                }
                sb.append("\n");
            }
            return sb.toString();
        }


    }

}

