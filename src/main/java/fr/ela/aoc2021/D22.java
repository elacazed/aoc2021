package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class D22 extends AoC {

    private static final Pattern RANGES_PATTERN = Pattern.compile("x=([-0-9]+)\\.\\.([-0-9]+),y=([-0-9]+)\\.\\.([-0-9]+),z=([-0-9]+)\\.\\.([-0-9]+)");

    @Override
    public void run() {
        resolve("Test", getTestInputPath());
        resolve("Real", getInputPath());
    }

    private void resolve(String test, Path path) {
        Reactor reactor = new Reactor(50);
        for (String line : list(path)) {
            reactor.apply(line);
        }
        System.out.println(test + " Boot Sequence Part 1 : " + reactor.onCubes.size());

    }


    public class Reactor {

        int minX, minY, minZ;
        int maxX, maxY, maxZ;

        Set<Cube> onCubes = new HashSet<>();

        public Reactor(int max) {
            minX = -1 * max;
            minY = -1 * max;
            minZ = -1 * max;
            maxX = max;
            maxY = max;
            maxZ = max;
        }

        public void switchCubes(Consumer<Cube> sw, int x1, int x2, int y1, int y2, int z1, int z2) {
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        sw.accept(Cube.of(x, z, y));
                    }
                }
            }

        }

        public void off(Cube c) {
            if (c != null) {
                onCubes.remove(c);
            }
        }

        public void on(Cube c) {
            if (c != null) {
                onCubes.add(c);
            }
        }

        void apply(String s) {
            boolean on = s.startsWith("on");
            int index = on ? 3 : 4;
            Matcher m = RANGES_PATTERN.matcher(s.substring(index));
            if (m.matches()) {
                int x1 = Integer.parseInt(m.group(1));
                int x2 = Integer.parseInt(m.group(2));
                int y1 = Integer.parseInt(m.group(3));
                int y2 = Integer.parseInt(m.group(4));
                int z1 = Integer.parseInt(m.group(5));
                int z2 = Integer.parseInt(m.group(6));

                x1 = Math.max(x1, minX);
                x2 = Math.min(x2, maxX);
                y1 = Math.max(y1, minY);
                y2 = Math.min(y2, maxY);
                z1 = Math.max(z1, minZ);
                z2 = Math.min(z2, maxZ);

                switchCubes(on ? this::on : this::off, x1, x2, y1, y2, z1, z2);
            }
        }
    }


    public record Cube(int x, int y, int z) {

        public static Cube of(int x, int y, int z) {
            if (Math.abs(x) > 50 || Math.abs(y) > 50 || Math.abs(z) > 50) {
                return null;
            }
            return new Cube(x, y, z);
        }
    }


}

