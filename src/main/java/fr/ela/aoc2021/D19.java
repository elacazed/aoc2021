package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;


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
        resolve("Test", getTestInputPath());
    }

    public void resolve(String name, Path path) {
        List<ScannerOrientation> scanners = readScanners(path).stream().map(ScannerOrientation::new).collect(Collectors.toList());

        var orientation = new Scanner[scanners.size()];
        var signals = new Signal[scanners.size()];

        // On prend le premier scanner comme base, sans le réorienter.
        orientation[0] = scanners.get(0).get(0, 0);
        signals[0] = new Signal(0, 0, 0);

        Queue<Integer> frontier = new ArrayDeque<>();
        frontier.add(0);

        while (!frontier.isEmpty()) {
            var front = frontier.poll();
            for (int i = 0; i < scanners.size(); i++) {
                if (signals[i] == null) {
                    if (scanners.get(front).get(0, 0).fingerMatch(scanners.get(i).get(0, 0))) {
                        var match = orientation[front].match(scanners.get(i));
                        if (match != null) {
                            orientation[i] = match.a; // correct orientation!
                            signals[i] = signals[front].relativeTo(match.b);
                            frontier.add(i);
                        }
                    }
                }
            }
        }

        var result = orientation[0].copy();
        for (int i = 1; i < scanners.size(); i++) {
            result.add(orientation[i], signals[i]);
        }

        int maxDist = Integer.MIN_VALUE;
        for (int i = 0; i < signals.length; i++) {
            for (int j = 0; j < signals.length; j++) {
                var one = signals[i];
                var two = signals[j];
                var d = Math.abs(one.x - two.x) + Math.abs(one.y - two.y) + Math.abs(one.z - two.z);
                maxDist = Math.max(maxDist, d);
            }
        }

        System.out.println(name + " Probe Beacons : " + result.signals.size());
        System.out.println(name + " maximum distance : " + maxDist);

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

        public Signal align(Orientation orientation) {
            return up(orientation.up).rot(orientation.rot);
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

    }


    private record Orientation(int rot, int up) {
    }


    private static class ScannerOrientation {
        final Scanner[] variations;

        public ScannerOrientation(Scanner sc) {
            variations = new Scanner[24];
            for (int up = 0; up < 6; up++) {
                for (int rot = 0; rot < 4; rot++) {
                    variations[rot + up * 4] = new Scanner(sc, up, rot);
                }
            }
        }

        public Scanner get(int up, int rot) {
            return variations[rot + up * 4];
        }
    }


    public static class Scanner {

        int[][] fingerprint;
        List<Signal> signals = new ArrayList<>();

        public Scanner(Scanner sc, int up, int rot) {
            this.signals = new ArrayList<>();
            for (var b : sc.signals) {
                this.signals.add(b.up(up).rot(rot));
            }
        }

        public Scanner() {

        }


        public Scanner copy() {
            Scanner copy = new Scanner();
            copy.signals = new ArrayList<>(signals);
            return copy;
        }

        void addSignal(Signal signal) {
            this.signals.add(signal);
        }

        /**
         * Calcule une signature pour ce scanner à partir des valeurs des signaux qu'il capte (ie : les distances relatives des beacons entre eux)
         *
         * @return
         */
        public int[][] finger() {
            if (fingerprint == null) {
                fingerprint = new int[signals.size()][signals.size()];
                for (int i = 0; i < signals.size(); i++) {
                    for (int j = 0; j < signals.size(); j++) {
                        fingerprint[i][j] = signals.get(i).distance(signals.get(j));
                    }
                    Arrays.sort(fingerprint[i]);
                }
            }
            return fingerprint;
        }

        /**
         * Compare la signature de ce scanner avec un autre : si on a 12 matches de distances égales, on est ok.
         *
         * @param other
         * @return
         */
        public boolean fingerMatch(Scanner other) {
            for (int i = 0; i < signals.size(); i++) {
                for (int j = 0; j < other.signals.size(); j++) {
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
                            if (count >= 12) {
                                return true;
                            }
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
            for (int i = 0; i < signals.size(); i++) {
                for (int j = 0; j < other.signals.size(); j++) {
                    var mine = signals.get(i);
                    var their = other.signals.get(j);
                    var relx = their.x - mine.x;
                    var rely = their.y - mine.y;
                    var relz = their.z - mine.z;
                    int count = 0;
                    for (int k = 0; k < signals.size(); k++) {
                        if ((count + signals.size() - k) < 12) {
                            break; // not possible
                        }
                        for (int l = 0; l < other.signals.size(); l++) {
                            var m = signals.get(k);
                            var n = other.signals.get(l);
                            if ((relx + m.x) == n.x && (rely + m.y) == n.y && (relz + m.z) == n.z) {
                                count++;
                                if (count >= 12) {
                                    return new Signal(relx, rely, relz);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            return null;
        }

        /**
         * Try to match the given scanner (for all orientations) with our signals
         */
        public Pair<Scanner, Signal> match(ScannerOrientation other) {
            for (int i = 0; i < other.variations.length; i++) {
                var oriented = other.variations[i];
                var match = test(oriented);
                if (match != null) {
                    return Pair.of(oriented, match);
                }
            }
            return null;
        }

        /**
         * Add all unique signals from the other scanner (with relative position) to our beacon list
         */
        public void add(Scanner other, Signal relPos) {
            for (int l = 0; l < other.signals.size(); l++) {
                var n = other.signals.get(l);
                n = new Signal(n.x - relPos.x, n.y - relPos.y, n.z - relPos.z);
                if (!signals.contains(n)) {
                    signals.add(n);
                }
            }
        }

    }

}

