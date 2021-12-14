package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.function.Function;

public class D14 extends AoC {


    @Override
    public void run() {
        process(getTestInputPath(), "Test", 10);
        process(getTestInputPath(), "Test", 40);
        process(getInputPath(), "Real", 10);
        process(getInputPath(), "Real", 40);
    }

    public void process(Path input, String name, int steps) {
        List<String> list = list(input);
        String polymer = list.get(0);
        Map<String, Rule> rules = new HashMap<>();
        list.stream().skip(2)
                .map(Rule::new)
                .forEach(s -> rules.put(s.pair, s));

        CountingMap<String> countPairs = new CountingMap<>();
        for (int i = 1; i < polymer.length(); i++) {
            String pair = polymer.substring(i - 1, i+1);
            countPairs.add(pair, 1);
        }
        CountingMap<Character> countChars = new CountingMap<>();
        polymer.chars().forEach(i -> countChars.add((char) i, 1));


        for (int i = 0; i < steps; i++) {
            CountingMap<String> newPairs = new CountingMap<>();
            CountingMap<Character> newChars = new CountingMap<>();
            for (Rule rule : rules.values()) {
                Counter c = countPairs.get(rule.pair);
                if (c != null) {
                    List<String> pairs = rule.result;
                    for (String pair : pairs) {
                        newPairs.computeIfAbsent(pair, p -> new Counter()).inc(c.count);
                    }
                    newChars.add(rule.addition, c.count);
                }
            }
            countPairs = newPairs;
            countChars.addAll(newChars);
        }

        LongSummaryStatistics stats = countChars.values().stream().mapToLong(c -> c.count).summaryStatistics();
        long min = stats.getMin();
        long max = stats.getMax();
        System.out.println(name + " Diff Part 1 : " + (max - min));
    }


    public class Counter {
        long count = 0;

        void inc(long x) {
            count = count + x;
        }
        public String toString() {
            return Long.toString(count);
        }
    }

    public class CountingMap<T> extends HashMap<T, Counter> {

        public Counter add(T key, Counter c) {
            Counter c2 = super.computeIfAbsent(key, k -> new Counter());
            c2.inc(c.count);
            return c2;
        }


        public Counter add(T key, long count) {
            Counter c2 = super.computeIfAbsent(key, k -> new Counter());
            c2.inc(count);
            return c2;
        }

        public void addAll(CountingMap<T> newChars) {
            newChars.forEach((key, value) -> add(key, value));
        }
    }

    public class Rule implements Function<String, List<String>> {

        final String pair;
        final char addition;
        final List<String> result;

        public Rule(String s) {
            this(s.substring(0, 2), s.charAt(6));
        }

        public Rule(String pair, char addition) {
            this.pair = pair;
            this.addition = addition;
            char[] p1 = pair.toCharArray();
            p1[1] = addition;
            char[] p2 = pair.toCharArray();
            p2[0] = addition;
            this.result = List.of(new String(p1), new String(p2));
        }

        @Override
        public List<String> apply(String s) {
            if (s.equals(pair)) {
                return result;
            }
            return List.of(s);
        }
    }

}
