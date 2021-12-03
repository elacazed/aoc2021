package fr.ela.aoc2021;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class D03 extends AoC {


    public void run() {
        List<String> testInput = list(getTestInputPath());
        List<String> input = list(getInputPath());
        System.out.println("Part One Test Result : " + solvePartOne(testInput));
        System.out.println("Part One Real Result : " + solvePartOne(input));

        System.out.println("Part Two Test Result : " + solvePartTwo(testInput));
        System.out.println("Part Two Real Result : " + solvePartTwo(input));

    }


    public int solvePartOne(List<String> data) {
        int size = data.size();
        int elementLength = data.get(0).length();
        char[] gammaChars = new char[elementLength];
        char[] epsilonChars = new char[elementLength];
        for (int i = 0; i < elementLength; i++) {
            char most = most(data, size, i);
            gammaChars[i] = most;
            epsilonChars[i] = most == '0' ? '1' : '0';
        }
        int gammaValue = Integer.parseInt(new String(gammaChars), 2);
        int epsilonValue = Integer.parseInt(new String(epsilonChars), 2);
        return gammaValue * epsilonValue;
    }

    /**
     * Get the most encountered bit value at position
     *
     * @param position the position
     * @return '1' or '0'
     */
    private char most(List<String> data, int size, int position) {
        Iterator<Character> it = data.stream().map(s -> s.charAt(position)).iterator();
        long count = 0;
        while (it.hasNext() && count < (size / 2)) {
            if (it.next() == '1') {
                count++;
            }
        }
        return count >= size / 2 ? '1' : '0';
    }

    public int solvePartTwo(List<String> data) {
        String o2 = getLevel(data, oxygenComparator);
        String co2 = getLevel(data, co2Comparator);
        int o2Value = Integer.parseInt(o2, 2);
        int co2Value = Integer.parseInt(co2, 2);
        return o2Value*co2Value;
    }

    public String getLevel(List<String> data, Function<Integer, Comparator<List<String>>> comparatorFactory) {
        int position = 0;
        while (data.size() > 1) {
            Comparator<List<String>> comparator = comparatorFactory.apply(position);
            final int pos = position;
            data = data.stream().collect(Collectors.groupingBy(s -> s.charAt(pos)))
                    .values().stream().max(comparator).orElseThrow();
            position++;
        }
        return data.get(0);
    }

    private Function<Integer, Comparator<List<String>>> oxygenComparator = position ->
            (o1, o2) -> {
                if (o1.size() == o2.size()) {
                    return (o1.get(0).charAt(position) == '1' ? 1 : -1);
                } else {
                    return o1.size() - o2.size();
                }
            };

    private Function<Integer, Comparator<List<String>>> co2Comparator = position -> oxygenComparator.apply(position).reversed();

}
