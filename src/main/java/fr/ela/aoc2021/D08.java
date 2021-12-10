package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class D08 extends AoC {

    @Override
    public void run() {
        solve(getTestInputPath(), "Test");
        solve(getInputPath(), "Real");
    }


    public void solve(Path path, String name) {
        List<Entry> entries = list(path, Entry::new);
        long easyNumbers = entries.stream().flatMap(Entry::streamOutputs).filter(D08::isEasyNuber).count();
        System.out.println(name + " result part 1 : " + easyNumbers);
        System.out.println(name + " result part 2 : " + entries.stream().mapToInt(D08::decode).sum());
    }

    private static String sortString(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    private static boolean isEasyNuber(String number) {
        int length = number.length();
        return length == 2 || length == 3 || length == 4 || length == 7;
    }

    private static class Entry {

        private final List<String> inputs;
        private final List<String> outputs;

        public Entry(String value) {
            String[] values = value.split(" \\| ");
            inputs = Arrays.stream(values[0].split("\s+")).map(D08::sortString).collect(Collectors.toList());
            outputs = Arrays.stream(values[1].split("\s+")).map(D08::sortString).collect(Collectors.toList());
        }

        public Stream<String> streamOutputs() {
            return outputs.stream();
        }
    }

    public static int decode(Entry e) {
        BytesDecoder decoder = new BytesDecoder(e.inputs);
        double result = 0;
        int size = e.outputs.size();
        for (int i = 0; i < size; i++) {
            byte b = decoder.toByte(e.outputs.get(i));
            result = result + Math.pow(10, size - i - 1) * decoder.permutation.get(b);
        }
        return (int) result;
    }

    private static class BytesDecoder {
        final Map<Byte, Integer> permutation;

        public BytesDecoder(List<String> inputs) {
            permutation = findPermutation(inputs);
        }

        public byte toByte(char c) {
            byte b = 0;
            b |= 1 << (7 - (int) c);
            return b;
        }

        public byte toByte(String word) {
            byte b = 0;
            for (char c : word.toCharArray()) {
                b |= 1 << (7 - (int) c);
            }
            return b;
        }

        private Map<Byte, Integer> findPermutation(List<String> entries) {
            Map<Byte, Integer> permutation = new HashMap<>();
            entries.sort(Comparator.comparingInt(String::length));

            // Fréquences d'apparition des segments :
            List<Integer> frequencies = entries.stream()
                    .flatMapToInt(String::chars)
                    .boxed().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet().stream()
                    .filter(e -> e.getValue() == 6 || e.getValue() == 4 || e.getValue() == 9)
                    .sorted(Comparator.comparingLong(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            byte e = toByte((char) frequencies.get(0).intValue());
            byte b = toByte((char) frequencies.get(1).intValue());
            byte f = toByte((char) frequencies.get(2).intValue());
            // Combinaisons de caractères
            byte cf = toByte(entries.get(0)); // 1 (longueur 2)
            byte acf = toByte(entries.get(1)); // 7 (longueur 3)
            byte bcdf = toByte(entries.get(2)); // 4 (longueur 4)
            byte abcdefg = toByte(entries.get(9)); // 8 (longueur 8)

            byte a = (byte) (acf ^ cf);
            byte c = (byte) (cf ^ f);
            byte d = (byte) (bcdf ^ f ^ c ^ b);
            byte g = (byte) (abcdefg ^ bcdf ^ a ^ e);

            permutation.put((byte) (a | b | c | e | f | g), 0);
            permutation.put(cf, 1);
            permutation.put((byte) (a | c | d | e | g), 2);
            permutation.put((byte) (a | c | d | f | g), 3);
            permutation.put(bcdf, 4);
            permutation.put((byte) (a | b | d | f | g), 5);
            permutation.put((byte) (a | b | d | e | f | g), 6);
            permutation.put(acf, 7);
            permutation.put(abcdefg, 8);
            permutation.put((byte) (a | b | c | d | f | g), 9);

            return permutation;
        }

    }
}
