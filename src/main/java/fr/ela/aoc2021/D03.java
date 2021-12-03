package fr.ela.aoc2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class D03 extends AoC {


    public void run() {
        Accumulator testAcc = partOne(stream(getTestInputPath()));
        Accumulator realAcc = partOne(stream(getInputPath()));

        testAcc.printResultPartTwo();
        realAcc.printResultPartTwo();
    }

    public static Accumulator partOne(Stream<String> input) {
        Accumulator acc = new Accumulator(input.collect(Collectors.toList()));
        acc.printResultPartOne();
        return acc;
    }

    public enum Bit {
        ONE,
        ZERO;

        public static Bit get(char c) {
            if (c == '1') {
                return ONE;
            } else {
                return ZERO;
            }
        }
    }

    public static class Accumulator {
        private List<String> data;
        final int size;
        final int elementLength;

        public Accumulator(List<String> data) {
            this.size = data.size();
            this.data = Collections.unmodifiableList(data);
            this.elementLength = data.get(0).length();
        }

        /**
         * Get the most encountered bit value at position
         *
         * @param position the position
         * @return '1' or '0'
         */
        char most(int position) {
            Iterator<Character> it = data.stream().map(s -> s.charAt(position)).iterator();
            long count = 0;
            while (it.hasNext() && count < (size / 2)) {
                if (it.next() == '1') {
                    count++;
                }
            }
            return count >= size / 2 ? '1' : '0';
        }

        public void printResultPartTwo() {
            String oxygen = getOxygen(-1);
            String co2 = getCo2(-1);
            System.out.println("Oxygen Rating [" + oxygen + "] Co2 Rating [" + co2 + "] => " + Integer.parseInt(co2, 2) * Integer.parseInt(oxygen, 2));
        }

        public void printResultPartOne() {
            char[] gammaChars = new char[elementLength];
            char[] epsilonChars = new char[elementLength];
            for (int i = 0; i < elementLength; i++) {
                char most = most(i);
                gammaChars[i] = most;
                epsilonChars[i] = most == '0' ? '1' : '0';
            }
            int gammaValue = Integer.parseInt(new String(gammaChars), 2);
            int epsilonValue = Integer.parseInt(new String(epsilonChars), 2);
            System.out.println("Gamma : " + gammaValue + ", Epsilon : " + epsilonValue + " -> " + gammaValue * epsilonValue);
        }

        private Map<Bit, List<String>> mapByPosition(int position) {
            return data.stream().collect(Collectors.groupingBy(s -> Bit.get(s.charAt(position))));
        }

        private Accumulator nextAccumulatorForOxygen(int position) {
            return new Accumulator(getLongestSublist(mapByPosition(position), position));
        }

        private Accumulator nextAccumulatorForCO2(int position) {
            return new Accumulator(getShortestSublist(mapByPosition(position), position));
        }

        private List<String> getLongestSublist(Map<Bit, List<String>> map, int pos) {
            return map.values().stream().max(comparator('1', pos)).orElseThrow();
        }

        private List<String> getShortestSublist(Map<Bit, List<String>> map, int pos) {
            return map.values().stream().min(comparator('1', pos)).orElseThrow();
        }

        private Comparator<List<String>> comparator(char value, int pos) {
            return (o1, o2) -> {
                if (o1.size() == o2.size()) {
                    return (o1.get(0).charAt(pos) == value ? 1 : -1);
                } else {
                    return o1.size() - o2.size();
                }
            };
        }

        private String getOxygen(int position) {
            if (size == 1) {
                return data.get(0);
            } else {
                int pos = position + 1;
                return nextAccumulatorForOxygen(pos).getOxygen(pos);
            }
        }

        private String getCo2(int position) {
            if (size == 1) {
                return data.get(0);
            } else {
                int pos = position + 1;
                return nextAccumulatorForCO2(pos).getCo2(pos);
            }
        }

    }

}
