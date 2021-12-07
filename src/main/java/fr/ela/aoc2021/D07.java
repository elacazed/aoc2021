package fr.ela.aoc2021;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class D07 extends AoC {
    @Override
    public void run() {
        List<Integer> crabsPosition = Arrays.stream(readFile(getTestInputPath())
                .split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        System.out.println("Solution Test partie 1 : "+getFuel(crabsPosition));

        crabsPosition = Arrays.stream(readFile(getInputPath())
                .split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        System.out.println("Solution partie 1 : "+getFuel(crabsPosition));

    }


    private int getFuel(List<Integer> crabsPosition) {
        crabsPosition.sort(Comparator.naturalOrder());
        Integer min = crabsPosition.get(0);
        Integer max = crabsPosition.get(crabsPosition.size() - 1);

        int[] fuels = new int [max - min + 1];
        for (int i = min; i <= max; i++) {
            final int target = i;
            fuels[i - min] = crabsPosition.stream()
                    .mapToInt(p -> Math.abs(p - target))
                    .sum();
        }
        int fuel = Arrays.stream(fuels).min().orElseThrow();
        return fuel;
    }


}
