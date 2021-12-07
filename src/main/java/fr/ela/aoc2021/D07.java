package fr.ela.aoc2021;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class D07 extends AoC {
    @Override
    public void run() {
        List<Integer> crabsPosition = Arrays.stream(readFile(getTestInputPath())
                .split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        System.out.println("Solution Test partie 1 : " + getFuel(crabsPosition, consoPart1));
        System.out.println("Solution Test mediane : " + mediane(crabsPosition));

        System.out.println("Solution Test partie 2 : " + getFuel(crabsPosition, consoPart2));
        crabsPosition = Arrays.stream(readFile(getInputPath())
                .split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        System.out.println("Solution partie 1 : " + getFuel(crabsPosition, consoPart1));
        System.out.println("Solution mediane : " + mediane(crabsPosition));
        System.out.println("Solution partie 2 : " + getFuel(crabsPosition, consoPart2));
    }

    private int mediane(List<Integer> crabsPosition) {
        crabsPosition.sort(Comparator.naturalOrder());
        int nb = crabsPosition.size();
        int position = nb % 2 == 1 ? (nb / 2) + 1 : nb / 2;
        final int mediane = crabsPosition.get(position);

        return crabsPosition.stream()
                .mapToInt(p -> consoPart1.apply(mediane).apply(p))
                .sum();
    }


    private int getFuel(List<Integer> crabsPosition, Function<Integer, IntFunction<Integer>> conso) {
        crabsPosition.sort(Comparator.naturalOrder());
        Integer min = crabsPosition.get(0);
        Integer max = crabsPosition.get(crabsPosition.size() - 1);
        int targetPosition = -1;
        int minFuel = -1;
        for (int i = min; i <= max; i++) {
            final int target = i;
            int fuel = crabsPosition.stream()
                    .mapToInt(p -> conso.apply(target).apply(p))
                    .sum();

            int minFuel1 = minFuel == -1 ? fuel : Math.min(fuel, minFuel);
            if (minFuel1 != minFuel) {
                minFuel = minFuel1;
                targetPosition = i;
            }
        }
        System.out.println("Position cible : "+targetPosition);
        return minFuel;
    }

    private Function<Integer, IntFunction<Integer>> consoPart1 = target ->
            p -> Math.abs(p - target);

    // 1+2...+n = n(n+1)/2
    private Function<Integer, IntFunction<Integer>> consoPart2 = target ->
            p -> {
                int n = Math.abs(p - target);
                return n * (n + 1) / 2;
            };


}
