package fr.ela.aoc2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class D06 extends AoC {

    @Override
    public void run() {
        List<Fish> fishes = fishes(readFile(getTestInputPath()));
        spendDays(fishes, 80);
        System.out.println("Test result part 1 : "+fishes.size());
        List<Fish> realfishes = fishes(readFile(getInputPath()));
        spendDays(realfishes, 80);
        System.out.println("Real result part 1 : "+realfishes.size());
    }


    public static void spendDays(List<Fish> fishes, int days) {
        for (int i = 0; i < days; i++) {
            day(fishes);
        }
    }

    static void day(List<Fish> fishes) {
        fishes.addAll(fishes.stream().map(Fish::day).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    static List<Fish> fishes(String s) {
        return Arrays.stream(s.split(",")).map(Integer::valueOf).map(Fish::new).collect(Collectors.toCollection(ArrayList::new));
    }

    static class Fish {
        public int timer;

        public Fish(int timer) {
            this.timer = timer;
        }

        public Fish day() {
            timer --;
            if (timer == -1) {
                timer = 6;
                return new Fish(8);
            } else {
                return null;
            }
        }
    }

}
