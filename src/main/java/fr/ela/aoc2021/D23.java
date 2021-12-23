package fr.ela.aoc2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class D23 extends AoC {

    public static char[][] TABLE = {
            "#############".toCharArray(),
            "#...........#".toCharArray(),
            "###.#.#.#.###".toCharArray(),
            "  #.#.#.#.#  ".toCharArray(),
            "  #########  ".toCharArray()};

    public static Map<Spot, Amphipod> REAL_MAP = Map.of(
            Spot.A1, new Amphipod('C'),
            Spot.A2, new Amphipod('C'),
            Spot.B1, new Amphipod('A'),
            Spot.B2, new Amphipod('A'),
            Spot.C1, new Amphipod('B'),
            Spot.C2, new Amphipod('D'),
            Spot.D1, new Amphipod('D'),
            Spot.D2, new Amphipod('B')
    );

    @Override
    public void run() {
        partOne();
    }

    public void partOne() {
        Game g = new Game(REAL_MAP);
        g.move(Spot.D1, Spot.H9);
        System.out.println(g);
        g.move(Spot.B1, Spot.H0);
        System.out.println(g);
        g.move(Spot.B2, Spot.H1);
        System.out.println(g);
        g.move(Spot.D2, Spot.B2);
        System.out.println(g);
        g.move(Spot.C1, Spot.B1);
        System.out.println(g);
        g.move(Spot.C2, Spot.D2);
        System.out.println(g);
        g.move(Spot.H9, Spot.D1);
        System.out.println(g);
        g.move(Spot.A1, Spot.C2);
        System.out.println(g);
        g.move(Spot.A2, Spot.C1);
        System.out.println(g);
        g.move(Spot.H1, Spot.A2);
        System.out.println(g);
        g.move(Spot.H0, Spot.A1);
        System.out.println(g);
        if (g.done()) {
            System.out.println("Part one score : " + g.cost());
        } else {
            System.out.println("Fail!");
        }
    }


    public class Game {

        List<Amphipod> amphipods;
        Map<Spot, Amphipod> spots;

        public Game(Map<Spot, Amphipod> start) {
            amphipods = new ArrayList<>(start.values());
            spots = new HashMap<>(start);
            for (Map.Entry<Spot, Amphipod> e : spots.entrySet()) {
                e.getValue().spot = e.getKey();
            }
        }

        boolean free(List<Spot> path) {
            return path.stream().noneMatch(spots::containsKey);
        }

        public int cost() {
            return amphipods.stream().mapToInt(Amphipod::cost).sum();
        }

        boolean moveTo(Amphipod a, Spot to) {
            if (! to.stopAllowed) {
                throw new IllegalArgumentException();
            }
            if (a.spot.equals(to)) {
                return false;
            }
            List<Spot> path = a.spot.path(to);

            if (!free(path)) {
                return false;
            }
            //move
            a.moves += path.size();
            System.out.println("Moving " + a.type + " from " + a.spot + " to " + to + " : " + path.stream().map(Enum::name).collect(Collectors.joining(",")));
            spots.remove(a.spot).spot = to;
            spots.put(to, a);
            return true;
        }

        boolean move(Spot sa, Spot sb) {
            Amphipod a = spots.get(sa);
            if (a == null) {
                throw new IllegalStateException();
            } else {
                return moveTo(a, sb);
            }
        }

        public boolean done() {
            return amphipods.stream().allMatch(Amphipod::isInHisRoom);
        }

        public String toString() {
            int length = TABLE[0].length;
            char[][] table = new char[TABLE.length][TABLE[0].length];
            for (int i = 0; i < TABLE.length; i++) {
                System.arraycopy(TABLE[i], 0, table[i], 0, length);
            }
            for (Amphipod a : amphipods) {
                int[] coords = a.spot.coords();
                table[coords[0]][coords[1]] = a.type;
            }
            return Arrays.stream(table).map(String::new).collect(Collectors.joining("\n"));
        }
    }


    public static class Amphipod {

        final char type;
        int moves;
        Spot spot;
        final int factor;

        EnumSet<Spot> destination;

        public Amphipod(char type) {
            this.type = type;
            this.moves = 0;
            destination = Spot.rooms.get(type);
            factor = factor(type);
        }

        public boolean isInHisRoom() {
            return spot.room == type;
        }

        private static int factor(char type) {
            switch (type) {
                case 'A':
                    return 1;
                case 'B':
                    return 10;
                case 'C':
                    return 100;
                case 'D':
                    return 1000;
                default:
                    return 0;
            }
        }

        public int cost() {
            return factor * moves;
        }
    }


    enum Spot {
        A1, A2,
        B1, B2,
        C1, C2,
        D1, D2,
        H0, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10;

        static Spot HALLWAY[] = {H0, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10};

        static Map<Character, EnumSet<Spot>> rooms = Map.of(
                'A', EnumSet.of(A1, A2),
                'B', EnumSet.of(B1, B2),
                'C', EnumSet.of(C1, C2),
                'D', EnumSet.of(D1, D2)
        );

        final int pos;
        final int hallwayPos;
        final char room;
        final boolean stopAllowed;

        Spot() {
            room = name().charAt(0);
            if (room == 'H') {
                pos = 0;
                hallwayPos = Integer.parseInt(name().substring(1));
                stopAllowed = (hallwayPos % 2 == 1) || hallwayPos == 0 || hallwayPos == 10;
            } else {
                pos = Character.getNumericValue(name().charAt(1));
                hallwayPos = getHallwayPos(room);
                stopAllowed = true;
            }
        }

        static Spot get(char room, int index) {
            return valueOf(Character.toString(room)+index);
        }

        boolean isInHallway() {
            return room == 'H';
        }

        static int getHallwayPos(char room) {
            switch (room) {
                case 'A':
                    return 2;
                case 'B':
                    return 4;
                case 'C':
                    return 6;
                case 'D':
                    return 8;
            }
            throw new IllegalArgumentException();
        }

        public int[] coords() {
            return new int[]{pos + 1, hallwayPos + 1};
        }

        List<Spot> path(Spot to) {
            List<Spot> path = new LinkedList<>();
            // Go up.
            for (int i = pos; i > 0; i--) {
                path.add(get(room, i));
            }
            // go left or right
            int start = hallwayPos;
            int inc = 1;
            Predicate<Integer> stop = i -> i <= to.hallwayPos;
            if (hallwayPos > to.hallwayPos) {
                inc = -1;
                stop = i -> i >= to.hallwayPos;
            }
            for (int i = start; stop.test(i); i+= inc) {
                path.add(HALLWAY[i]);
            }
            // Go down
            for (int i = 1; i <= to.pos; i++) {
                path.add(get(to.room, i));
            }
            // Remove starting position
            path.remove(0);
            return path;
        }
    }

}
