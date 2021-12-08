package fr.ela.aoc2021;

import org.w3c.dom.ElementTraversal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class D08 extends AoC {
    @Override
    public void run() {
        List<Entry> testEntries = list(getTestInputPath(), Entry::new);
        long easyNumbers = testEntries.stream().flatMap(Entry::streamOutputs).filter(D08::isEasyNuber).count();
        System.out.println("Test result part 1 : "+easyNumbers);
        List<Entry> entries = list(getInputPath(), Entry::new);
        easyNumbers = entries.stream().flatMap(Entry::streamOutputs).filter(D08::isEasyNuber).count();
        System.out.println("Real result part 1 : "+easyNumbers);
    }


    private static boolean isEasyNuber(String number) {
        int length = number.length();
        return length == 2 || length == 3 || length == 4 || length == 7;
    }

    private class Entry {
        private String[] inputs;
        private String[] outputs;

        public Entry(String value) {
            String[] values = value.split(" \\| ");
            inputs = values[0].split("\s+");
            outputs = values[1].split("\s+");
        }

        public Stream<String> streamOutputs() {
            return Arrays.stream(outputs);
        }
        public Stream<String> streamInputs() {
            return Arrays.stream(inputs);
        }

    }

}
