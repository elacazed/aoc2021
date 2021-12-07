package fr.ela.aoc2021;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class D07 extends AoC {
    @Override
    public void run() {
        List<Integer> testCrabsPositions = oneLineList(getTestInputPath(), ",", Integer::parseInt);
        System.out.println("Solution Test partie 1 : " + getMinFuelConsumption(testCrabsPositions, consoPart1));
        System.out.println("Solution Test partie 2 : " + getMinFuelConsumption(testCrabsPositions, consoPart2));

        List<Integer> crabsPositions = oneLineList(getTestInputPath(), ",", Integer::parseInt);
        System.out.println("Solution partie 1 : " + getMinFuelConsumption(crabsPositions, consoPart1));
        System.out.println("Solution partie 2 : " + getMinFuelConsumption(crabsPositions, consoPart2));
    }

    /*
     * Calcule la consommation de tous les crabes pour atteindre la position cible en utilisant la fonction de consommation donnée.
     */
    private int fuelConsumption(List<Integer> crabsPositions, BiFunction<Integer, Integer, Integer> conso, int target) {
        Function<Integer, Integer> consoFunction = p -> conso.apply(p, target);
        return crabsPositions.stream().mapToInt(consoFunction::apply).sum();
    }

    /*
     * On cherche la valeur minimale de consommation pour les différentes positions cibles possibles,
     * Soit entre les positions minimale et maximale des crabes, inclues.
     */
    private int getMinFuelConsumption(List<Integer> crabsPosition, BiFunction<Integer, Integer, Integer> conso) {
        IntSummaryStatistics stats = crabsPosition.stream().mapToInt(Integer::intValue).summaryStatistics();
        // Max is exclusive in range.
        return IntStream.range(stats.getMin(), stats.getMax() + 1).map(t -> fuelConsumption(crabsPosition, conso, t)).min().orElseThrow();
    }

    /*
     * Fonction de calcul de la consommation partie 1 :
     * prend en entrée la position du crabe et la position cible, retourne l'écart absolu.
     */
    private final BiFunction<Integer, Integer, Integer> consoPart1 = (position, target) -> Math.abs(position - target);
    /*
     * Fonction de calcul de la consommation partie 2 :
     * prend en entrée la position du crabe et la position cible, retourne la somme des entiers de 0 à l'écart absolu.
     * Soit n(n+1)/2 avec n = écart absolu = résultat de la fonction partie 1.
     * Donc on compose.
     */
    private final BiFunction<Integer, Integer, Integer> consoPart2 = consoPart1.andThen(n -> n * (n + 1) / 2);



}
