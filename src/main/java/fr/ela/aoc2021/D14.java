package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D14 extends AoC {

    @Override
    public void run() {
        process(getTestInputPath(), "Test");
        long time = System.nanoTime();
        process(getInputPath(), "Real");
        System.out.println("Time : "+ ((System.nanoTime() - time)/1000000) +" milliseconds");
    }

    public void process(Path input, String name) {
        List<String> list = list(input);
        String polymer = list.get(0);
        List<Rule> rules = list.stream().skip(2).map(Rule::new).collect(Collectors.toList());

        CountingMap<String> countPairs = new CountingMap<>();
        IntStream.range(1, polymer.length()).mapToObj(i -> polymer.substring(i-1, i+1)).forEach(p -> countPairs.add(p, 1));

        CountingMap<Character> countChars = new CountingMap<>();
        polymer.chars().forEach(i -> countChars.add((char) i, 1));
        repeat(10, i -> step(rules, countPairs, countChars));
        LongSummaryStatistics stats = countChars.values().stream().mapToLong(c -> c.count).summaryStatistics();
        System.out.println(name + " Diff after 10 steps : " + (stats.getMax() - stats.getMin()));
        repeat(30, i -> step(rules, countPairs, countChars));
        stats = countChars.values().stream().mapToLong(c -> c.count).summaryStatistics();
        System.out.println(name + " Diff after 40 steps : " + (stats.getMax() - stats.getMin()));
        System.out.println(name + " total characters : " + stats.getSum());
    }

    public void step(List<Rule> rules, CountingMap<String> pairs, CountingMap<Character> chars) {
        CountingMap<String> newPairs = new CountingMap<>();
        rules.forEach(r -> r.apply(pairs, chars, newPairs));
        pairs.clear();
        pairs.putAll(newPairs);
    }

    public static class Counter {
        long count = 0;

        void inc(long x) {
            count = count + x;
        }
    }

    public static class CountingMap<T> extends HashMap<T, Counter> {
        public void add(T key, long count) {
            super.computeIfAbsent(key, k -> new Counter()).inc(count);
        }
    }

    public static class Rule {

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

        public void apply(CountingMap<String> pairs, CountingMap<Character> chars, CountingMap<String> newPairs) {
            Counter c = pairs.get(pair);
            if (c != null) {
                chars.add(addition, c.count);
                result.forEach(v -> newPairs.add(v, c.count));
            }

        }
    }

}
