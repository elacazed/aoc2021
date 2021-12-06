package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class D06 extends AoC {

    @Override
    public void run() {
        FishPopulation testPopulation = readPopulation(getTestInputPath());
        testPopulation.shift(80);
        System.out.println("Test result part 1 : "+testPopulation.total());

        FishPopulation population = readPopulation(getInputPath());
        population.shift(80);
        System.out.println("Real result part 1 : "+population.total());

        testPopulation.shift(256 - 80);
        System.out.println("Test result part 2 : "+testPopulation.total());
        population.shift(256 - 80);
        System.out.println("Real result part 2 : "+population.total());
    }

    FishPopulation readPopulation(Path input) {
        return new FishPopulation(Arrays.stream(readFile(input).split(",")).map(Integer::parseInt).collect(Collectors.toList()));
    }

    static class FishPopulation {
        long[] ages;
        public FishPopulation(List<Integer> fishes) {
            ages = new long[9];
            Arrays.fill(ages, 0);
            for (int age : fishes) {
                ages[age]++;
            }
        }
        void shift(int days) {
            for (int d = 0; d < days; d++) {
                long nb0 = ages[0];
                System.arraycopy(ages, 1, ages, 0, 8);
                ages[6] += nb0;
                ages[8] = nb0;
            }
        }

        long total() {
            return LongStream.of(ages).sum();
        }
    }

}
