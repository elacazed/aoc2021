package fr.ela.aoc2021;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class D17 extends AoC {

    private final Target TEST_TARGET = new Target(20, -10, 30, -5);
    private final Target REAL_TARGET = new Target(137, -98, 171, -73);

    // Partie 1
    // La vitesse à laquelle on passe l'axe Y en descendant est égale à -vYo
    // A ce moment là on ajoutera -vYo à Y et on veut arriver dans la zone, le plus loin possible => à min(y) dans la cible.
    // => - vYo = Y min de la zone cible.

    // xMin = 1 (on veut aller vers l'avant, mais au second tour on va se retrouver à 0!!
    // xMax = max(x) de la zone cible.
    // On a intérêt à partir de xMax, et à s'arrêter si on a eu des hits avant, et qu'on va plus haut sans toucher la cible.

    @Override
    public void run() {
        Probe test = findHighestProbe(TEST_TARGET);
        System.out.println("Test shooting : " + test.maxY);

        Probe probe = findHighestProbe(REAL_TARGET);
        System.out.println("Probe shooting : " + probe.maxY);
    }

    public Probe findHighestProbe(Target target) {
        int maxVY = Math.max(Math.abs(target.yMin), Math.abs(target.yMax));
        int minVY = Math.min(Math.abs(target.yMin), Math.abs(target.yMax));
        Probe max = null;
        for (int vx = target.xMax; vx > 1; vx--) {
            for (int vy = minVY; vy <= maxVY; vy++) {
                Probe p = shoot(target, vx, vy);
                if (target.hit(p.pos)) {
                    max = (max == null || p.maxY > max.maxY) ? p : max;
                }
                if (max != null && p.maxY > max.maxY) {
                    // on a tiré trop haut avec vx0 trop petit.
                    return max;
                }
            }
        }
        return max;
    }


    public Probe shoot(Target target, int vx, int vy) {
        Probe probe = new Probe(vx, vy);
        while (!target.hit(probe.pos) && !target.offTarget(probe.pos)) {
            probe.step();
        }
        return probe;
    }

    static final boolean between(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public class Probe {
        final int vx0;
        final int vy0;
        int vy;
        int vx;
        int maxY = 0;
        Position pos;

        public Probe(int vx, int vy) {
            this.vx0 = vx;
            this.vy0 = vy;
            this.vx = vx;
            this.vy = vy;
            pos = new Position(0, 0);
            maxY = 0;
        }

        public void step() {
            int x = pos.x + vx;
            int y = pos.y + vy;
            if (vx != 0) {
                vx = vx > 0 ? vx - 1 : vx + 1;
            }
            vy--;
            pos = new Position(x, y);
            maxY = Math.max(pos.y, maxY);
        }


    }

    public record Target(int xMin, int yMin, int xMax, int yMax) {

        public Target(int xMin, int yMin, int xMax, int yMax) {
            this.xMin = Math.min(xMin, xMax);
            this.xMax = Math.max(xMin, xMax);
            this.yMin = Math.min(yMin, yMax);
            this.yMax = Math.max(yMin, yMax);
        }

        boolean hit(Position pos) {
            return between(pos.x, xMin, xMax) && between(pos.y, yMin, yMax);
        }

        boolean offTarget(Position pos) {
            return pos.x > xMax || pos.y < yMin;
        }
    }

    public record Position(int x, int y) {
    }


}

