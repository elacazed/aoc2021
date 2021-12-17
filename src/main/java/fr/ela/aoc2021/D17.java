package fr.ela.aoc2021;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class D17 extends AoC {

    private final Target TEST_TARGET = new Target(20, -10, 30, -5);
    private final Target REAL_TARGET = new Target(137, -98, 171, -73);

    // Partie 1
    // La vitesse à laquelle on passe l'axe Y en descendant est égale à -vYo
    // A ce moment là on ajoutera -vYo à Y et on veut arriver dans la zone, le plus loin possible => à min(y) dans la cible.
    // => vYo = |Y min| de la zone cible (-1, parce que vY augmente de 1 à chaque étape)
    // si on connaît vY0 alors on connaît l'altitude max : c'est l'altitude à laquelle vY est nulle.
    // Et c'est aussi la somme des vY pour chaque step, soit vY0 + (vY0 -1) + ...+ 0 = vY0(vY0-1) / 2

    // Partie 2 :
    // vYmin = direct hit au coin en bas à gauche
    // vYmin = voir partie 1
    // vXmin = 1
    // vXmax = maxX de la cible.
    @Override
    public void run() {

        System.out.println("Test shooting highest altitude : " + maxAltitude(TEST_TARGET));
        List<Probe> testProbes = getAllHittingProbes(TEST_TARGET);
        System.out.println("Hitting initial velocities for Test target : "+testProbes.size());
        System.out.println("---");
        System.out.println("Probe shooting highest altitude : " + maxAltitude(REAL_TARGET));
        List<Probe> probes = getAllHittingProbes(REAL_TARGET);
        System.out.println("Hitting initial velocities : "+probes.size());
    }

    public int maxVy(Target target) {
        return Math.abs(target.yMin)-1;
    }

    public int maxAltitude(Target target) {
        int maxVy = maxVy(target);
        return (maxVy + 1) * maxVy / 2;
    }

    public List<Probe> getAllHittingProbes(Target target) {
        int maxVY = maxVy(target);
        List<Probe> probes = new ArrayList<>();
        for (int vx = target.xMax; vx > 1; vx--) {
            for (int vy = target.yMin; vy <= maxVY; vy++) {
                shoot(target, vx, vy).ifPresent(probes::add);
            }
        }
        return probes;
    }

    public Optional<Probe> shoot(Target target, int vx, int vy) {
        Probe probe = new Probe(vx, vy);
        while (!target.hit(probe) && !target.offTarget(probe)) {
            probe.step();
        }
        return target.hit(probe) ? Optional.of(probe) : Optional.empty();
    }

    static final boolean between(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public class Probe {
        int vy,vx,x,y;

        public Probe(int vx, int vy) {
            this.vx = vx;
            this.vy = vy;
            x = 0;
            y = 0;
        }

        public void step() {
            x = x + vx;
            y = y + vy;
            if (vx != 0) {
                vx = vx > 0 ? vx - 1 : vx + 1;
            }
            vy--;
        }
    }

    public record Target(int xMin, int yMin, int xMax, int yMax) {
        public Target(int xMin, int yMin, int xMax, int yMax) {
            this.xMin = Math.min(xMin, xMax);
            this.xMax = Math.max(xMin, xMax);
            this.yMin = Math.min(yMin, yMax);
            this.yMax = Math.max(yMin, yMax);
        }
        boolean hit(Probe probe) {
            return between(probe.x, xMin, xMax) && between(probe.y, yMin, yMax);
        }
        boolean offTarget(Probe pos) {
            return pos.x > xMax || pos.y < yMin;
        }
    }

}

