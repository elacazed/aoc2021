package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class D06 extends AoC {

    @Override
    public void run() {
        FishPopulation testPopulation = readPopulation(getTestInputPath());
        System.out.println("Test result part 1 : " + testPopulation.grow(80));

        FishPopulation population = readPopulation(getInputPath());
        System.out.println("Real result part 1 : " + population.grow(80));

        System.out.println("Test result part 2 : " + testPopulation.grow(256 - 80));
        System.out.println("Real result part 2 : " + population.grow(256 - 80));
    }

    FishPopulation readPopulation(Path input) {
        return new FishPopulation(Arrays.stream(readFile(input).split(",")).map(Integer::parseInt).collect(Collectors.toList()));
    }

    static class FishPopulation {
        // Number of fishes for each timer value
        private long[] ages;
        // Age of the population in days.
        private int age;

        public FishPopulation(List<Integer> fishes) {
            ages = new long[9];
            age = 0;
            Arrays.fill(ages, 0);
            for (int age : fishes) {
                ages[age]++;
            }
        }

        void grow() {
            ages[(age + 7) % 9] += ages[age % 9];
            age++;
        }

        long grow(int days) {
            IntStream.range(0, days).forEach(i -> this.grow());
            return LongStream.of(ages).sum();
        }
    }

}
