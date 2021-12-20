package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class D19 extends AoC {

    private static class Pair<A, B> {
        A a;
        B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public static <A, B> Pair<A, B> of(A a, B b) {
            return new Pair<>(a, b);
        }
    }

    @Override
    public void run() {
        part1(getTestInputPath());
        part1(getInputPath());
    }


    public List<Scanner> readScanners(Path path) {
        List<Scanner> scanners = new ArrayList<>();
        Scanner current = null;
        for (String line : list(path)) {
            if (line.length() == 0) {
                continue;
            }
            if (line.startsWith("---")) {
                if (current != null) {
                    scanners.add(current);
                }
                current = new Scanner();
                continue;
            }
            current.addSignal(parseSignal(line));
        }
        return scanners;
    }

    public static Signal parseSignal(String s) {
        String[] coords = s.split(",");
        return new Signal(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
    }

    public record Signal(int x, int y, int z) {

        public Signal(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Signal relativeTo(Signal other) {
            return new Signal(x + other.x, y + other.y, z + other.z);
        }

        public Signal(Signal one, Signal two) {
            this(one.x + two.x, one.y + two.y, one.z + two.z);
        }

        public Signal up(int up) {
            return switch (up) {
                case 0 -> this;
                case 1 -> new Signal(x, -y, -z);
                case 2 -> new Signal(x, -z, y);
                case 3 -> new Signal(-y, -z, x);
                case 4 -> new Signal(-x, -z, -y);
                case 5 -> new Signal(y, -z, -x);
                default -> null;
            };
        }

        public Signal rot(int rot) {
            return switch (rot) {
                case 0 -> this;
                case 1 -> new Signal(-y, x, z);
                case 2 -> new Signal(-x, -y, z);
                case 3 -> new Signal(y, -x, z);
                default -> null;
            };
        }

        public int distance(Signal other) {
            return Math.abs(other.x - x) + Math.abs(other.y - y) + Math.abs(other.z - z);
        }

        public Signal rotate(Orientation o) {
            return up(o.up).rot(o.rot);
        }
    }


    private static class ScannerOrientations {
        final Map<Orientation, Scanner> rotations;

        public ScannerOrientations(Scanner sc) {
            rotations = new HashMap<>();
            Orientation.ALL.forEach(r -> rotations.put(r, sc.rotate(r)));
        }

        public Scanner get(Orientation o) {
            return rotations.get(o);
        }
    }

    private record Orientation(int up, int rot) {

        private static List<Orientation> ALL = orientations();
        private static final Orientation BASE = new Orientation(0,0);

        static List<Orientation> orientations() {
            List<Orientation> orientations = new ArrayList<>();
            for (int up = 0; up < 6; up++) {
                for (int rot = 0; rot < 4; rot++) {
                    orientations.add(new Orientation(up, rot));
                }
            }
            return orientations;
        }

    }

    private static class Scanner {

        final List<Signal> beacons;

        int[][] fingerprint;

        public Scanner() {
            this.beacons = new ArrayList<>();
        }

        public Scanner(Scanner other) {
            this.beacons = new ArrayList<>(other.beacons);
        }

        public Scanner rotate(Orientation o) {
            Scanner scanner = new Scanner();
            beacons.stream().map(sc -> sc.rotate(o)).collect(Collectors.toCollection(() -> scanner.beacons));
            return scanner;
        }

        public void addSignal(Signal signal) {
            this.beacons.add(signal);
        }

        public int[][] finger() {
            if (fingerprint == null) {
                fingerprint = new int[beacons.size()][beacons.size()];
                for (int i = 0; i < beacons.size(); i++) {
                    for (int j = 0; j < beacons.size(); j++) {
                        fingerprint[i][j] = beacons.get(i).distance(beacons.get(j));
                    }
                    Arrays.sort(fingerprint[i]);
                }
            }
            return fingerprint;
        }

        public boolean fingerMatch(Scanner other) {
            for (int i = 0; i < beacons.size(); i++) {
                for (int j = 0; j < other.beacons.size(); j++) {
                    var p1 = finger()[i];
                    var p2 = other.finger()[j];
                    // check if fingerprint matches
                    int x = 0;
                    int y = 0;
                    int count = 0;
                    while (x < p1.length && y < p2.length) {
                        if (p1[x] == p2[y]) {
                            x++;
                            y++;
                            count++;
                            if (count >= 12) return true;
                        } else if (p1[x] > p2[y]) {
                            y++;
                        } else if (p1[x] < p2[y]) {
                            x++;
                        }
                    }
                }
            }
            return false;
        }

        private Signal test(Scanner other) {
            for (int i = 0; i < beacons.size(); i++) {
                for (int j = 0; j < other.beacons.size(); j++) {
                    var mine = beacons.get(i);
                    var their = other.beacons.get(j);
                    var relx = their.x - mine.x;
                    var rely = their.y - mine.y;
                    var relz = their.z - mine.z;
                    int count = 0;
                    for (int k = 0; k < beacons.size(); k++) {
                        if ((count + beacons.size() - k) < 12) break; // not possible
                        for (int l = 0; l < other.beacons.size(); l++) {
                            var m = beacons.get(k);
                            var n = other.beacons.get(l);
                            if ((relx + m.x) == n.x && (rely + m.y) == n.y && (relz + m.z) == n.z) {
                                count++;
                                if (count >= 12) return new Signal(relx, rely, relz);
                                break;
                            }
                        }
                    }
                }
            }
            return null;
        }

        /**
         * Try to match the given scanner (for all orientations) with our beacons
         */
        public Pair<Scanner, Signal> match(ScannerOrientations other) {
            for (Orientation o : Orientation.ALL) {
                var sc = other.rotations.get(o);
                var mat = test(sc);
                if (mat != null) return Pair.of(sc, mat);
            }
            return null;
        }

        /**
         * Add all unique beacons from the other scanner (with relative position) to our beacon list
         */
        public void add(Scanner other, Signal relPos) {
            for (int l = 0; l < other.beacons.size(); l++) {
                var n = other.beacons.get(l);
                n = new Signal(n.x - relPos.x, n.y - relPos.y, n.z - relPos.z);
                if (!beacons.contains(n)) beacons.add(n);
            }
        }

    }


    protected void part1(Path path) {
        var scanners = readScanners(path).stream().map(ScannerOrientations::new).collect(Collectors.toList());
        var orientation = new Scanner[scanners.size()];
        var position = new Signal[scanners.size()];

        orientation[0] = scanners.get(0).get(Orientation.BASE);
        position[0] = new Signal(0, 0, 0);

        Queue<Integer> frontier = new ArrayDeque<>();
        frontier.add(0);

        while (!frontier.isEmpty()) {
            var front = frontier.poll();
            for (int i = 0; i < scanners.size(); i++) {
                if (position[i] == null) {
                    if (scanners.get(front).get(Orientation.BASE).fingerMatch(scanners.get(i).get(Orientation.BASE))) {
                        var match = orientation[front].match(scanners.get(i));
                        if (match != null) {
                            orientation[i] = match.a; // correct orientation!
                            position[i] = new Signal(position[front], match.b);
                            frontier.add(i);
                        }
                    }
                }
            }
        }

        var result = new Scanner(orientation[0]);
        for (int i = 1; i < scanners.size(); i++) {
            result.add(orientation[i], position[i]);
        }

        int maxDist = Integer.MIN_VALUE;
        for (int i = 0; i < position.length; i++) {
            for (int j = 0; j < position.length; j++) {
                var one = position[i];
                var two = position[j];
                var d = Math.abs(one.x - two.x) + Math.abs(one.y - two.y) + Math.abs(one.z - two.z);
                maxDist = Math.max(maxDist, d);
            }
        }

        System.out.println("Part 1: " + result.beacons.size());
        System.out.println("Part 2: " + maxDist);
    }


}

