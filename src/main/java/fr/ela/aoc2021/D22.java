package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class D22 extends AoC {

    private static final Pattern RANGES_PATTERN = Pattern.compile("x=([-0-9]+)\\.\\.([-0-9]+),y=([-0-9]+)\\.\\.([-0-9]+),z=([-0-9]+)\\.\\.([-0-9]+)");

    @Override
    public void run() {
        resolve("Test", getTestInputPath());
        resolve("Real", getInputPath());
    }

    private void resolve(String test, Path path) {
        List<BootStep> steps = list(path, BootStep::parse);

        // Filter only steps applicable to boxed reactor.
        List<BootStep> boxed = steps.stream().map(s -> s.applicable(50)).filter(Objects::nonNull).collect(Collectors.toList());
        System.out.println(test + " Boot Sequence Part 1 : " + bootSequence(boxed));
        System.out.println(test + " Complete Boot Sequence Result : " + bootSequence(steps));
    }


    public record BootStep(boolean on, Cuboid cuboid) implements Function<List<Cuboid>, List<Cuboid>> {
        public static BootStep parse(String s) {
            boolean on = s.startsWith("on");
            int index = on ? 3 : 4;
            Cuboid cuboid = Cuboid.parse(s.substring(index));

            return new BootStep(on, cuboid);
        }

        public List<Cuboid> apply(List<Cuboid> previousState) {
            List<Cuboid> newLitCuboids = new ArrayList<>();
            for (Cuboid on : previousState) {
                if (on.isOverlapping(cuboid)) {
                    newLitCuboids.addAll(on.split(cuboid));
                } else {
                    newLitCuboids.add(on);
                }
            }
            if (on) {
                newLitCuboids.add(cuboid);
            }
            return newLitCuboids;
        }

        public Function<List<Cuboid>, List<Cuboid>> toFunc() {
            return this;
        }

        // Refacto part 1 : transform BootStep in another one limited to reactor size.
        public BootStep applicable(int max) {
            int[] x = overlap(-max, cuboid.x1, max, cuboid.x2);
            int[] y = overlap(-max, cuboid.y1, max, cuboid.y2);
            int[] z = overlap(-max, cuboid.z1, max, cuboid.z2);

            if (x[0] < x[1] && y[0] < y[1] && z[0] < z[1]) {
                return new BootStep(on, new Cuboid(x[0], x[1], y[0], y[1], z[0], z[1]));
            } else {
                return null;
            }
        }

    }

    public record Cuboid(int x1, int x2, int y1, int y2, int z1, int z2) {

        public static Cuboid parse(String s) {
            Matcher m = RANGES_PATTERN.matcher(s);
            if (m.matches()) {
                int x1 = Integer.parseInt(m.group(1));
                int x2 = Integer.parseInt(m.group(2));
                int y1 = Integer.parseInt(m.group(3));
                int y2 = Integer.parseInt(m.group(4));
                int z1 = Integer.parseInt(m.group(5));
                int z2 = Integer.parseInt(m.group(6));
                return new Cuboid(x1, x2, y1, y2, z1, z2);
            }
            throw new IllegalStateException();
        }

        public long volume() {
            // on a besoin des bords dans le volume.
            return (long) (1 + x2 - x1) * (1 + y2 - y1) * (1 + z2 - z1);
        }

        boolean isOverlapping(Cuboid otherCuboid) {
            return otherCuboid.x1 <= x2 && x1 <= otherCuboid.x2
                    && otherCuboid.y1 <= y2 && y1 <= otherCuboid.y2
                    && otherCuboid.z1 <= z2 && z1 <= otherCuboid.z2;
        }

        // Découpage de this en 6 morceaux qui ne contiennent pas les éléments de other.
        // Gaffe aux -1/+1 pour ne pas avoir la bordure en double!
        // Le "on" n'a pas d'intérêt : on enlève toujours les parties en commun.
        // C'est l'ajout du cube s'il est à "on" qui permet d'ajouter les cubes allumés.
        List<Cuboid> split(Cuboid other) {
            List<Cuboid> slices = new ArrayList<>();
            // Tranche à gauche
            if (other.x1 > x1) {
                slices.add(new Cuboid(x1, other.x1 - 1, y1, y2, z1, z2));
            }
            // tranche à droite
            if (other.x2 < x2) {
                slices.add(new Cuboid(other.x2 + 1, x2, y1, y2, z1, z2));
            }
            // tranche au dessus et en dessous, sur la largeur commune aux 2 cubes
            int[] overlapX = overlap(x1, other.x1, x2, other.x2);
            if (other.y1 > y1) {
                slices.add(new Cuboid(overlapX[0], overlapX[1], y1, other.y1 - 1, z1, z2));
            }
            if (other.y2 < y2) {
                slices.add(new Cuboid(overlapX[0], overlapX[1], other.y2 + 1, y2, z1, z2));
            }

            // tranche devant et derrière, sur la largeur et la hauteur communes aux 2 cubes
            int[] overlapY = overlap(y1, other.y1, y2, other.y2);
            if (other.z1 > z1) {
                slices.add(new Cuboid(overlapX[0], overlapX[1], overlapY[0], overlapY[1], z1, other.z1 - 1));
            }
            if (other.z2 < z2) {
                slices.add(new Cuboid(overlapX[0], overlapX[1], overlapY[0], overlapY[1], other.z2 + 1, z2));
            }
            return slices;
        }
    }

    // Segment commun à 2 segments qui se recouvrent.
    static int[] overlap(int min1, int min2, int max1, int max2) {
        return new int[]{Math.max(min1, min2), Math.min(max1, max2)};
    }

    public long bootSequence(List<BootStep> steps) {
        Function<List<Cuboid>, Long> bootSequence = steps.stream()
                .map(BootStep::toFunc) // fucking types.
                .reduce(Function::andThen).orElseThrow()
                .andThen(l -> l.stream().mapToLong(Cuboid::volume).sum());
        return bootSequence.apply(new ArrayList<>());
    }

}