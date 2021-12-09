package fr.ela.aoc2021;

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

        List<Entry> testEntries = list(getTestInputPath(), Entry::new);
        long easyNumbers = testEntries.stream().flatMap(Entry::streamOutputs).filter(D08::isEasyNuber).count();
        System.out.println("Test result part 1 : " + easyNumbers);
        List<Entry> entries = list(getInputPath(), Entry::new);
        easyNumbers = entries.stream().flatMap(Entry::streamOutputs).filter(D08::isEasyNuber).count();
        System.out.println("Real result part 1 : " + easyNumbers);

        int result = decode(new Entry("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf"));
        System.out.println("Result : "+result);

//        new BytesDecoder((new Entry("abcefg cf acdeg acdfg bcdf abdfg abdefg acf abcdefg abcdfg | cdfeb fcadb cdfeb cdbaf").inputs));
    }

    private static final String sortString(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    private static boolean isEasyNuber(String number) {
        int length = number.length();
        return length == 2 || length == 3 || length == 4 || length == 7;
    }

    private class Entry {

        private List<String> inputs;
        private List<String> outputs;

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
        final Map<String, Integer> realBytes = Map.of(
                "abcefg", 0,
                "cf", 1,
                "acdeg", 2,
                "acdfg", 3,
                "bcdf", 4,
                "abdfg", 5,
                "abdefg", 6,
                "acf", 7,
                "abcdefg", 8,
                "abcdfg", 9);

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

        public String fromByte(byte b) {
            char[] chars = new char[7];
            int i = 0;
            for (char c : "abcdefg".toCharArray()) {
                if ((b >> (7 - (int) c) & 1) != 0) {
                    chars[i] = c;
                    i++;
                }
            }
            return new String(chars, 0, i + 1);
        }

        public byte encode(String word) {
            byte b = 0;
            for (char c : word.toCharArray()) {
                b |= permutation.get(c);
            }

            return b;
        }

        /*
               0 = abc efg
             * 1 =   c  f
               2 = a cde g
               3 = a cd fg
             * 4 =  bcd f
               5 = ab d fg
               6 = ab defg
             * 7 = a c  f
               8 = abcdefg
             * 9 = abcd fg

               a : 8
                b : 6
               c : 8
               d : 7
                e : 4
                f : 9
               g : 7
               */
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
            byte cf = toByte(entries.get(0));

            byte acf = toByte(entries.get(1));
            byte bcdf = toByte(entries.get(2));
            byte abcdefg = toByte(entries.get(9));

            byte a = (byte) (acf ^ cf);

            byte c = (byte) (cf ^ f);
            byte d = (byte) (bcdf ^ f ^ c ^ b);
            byte g = (byte) (abcdefg ^ bcdf ^ a ^ e);

            permutation.put((byte) (a | b | c | e | f | g), 0);
            permutation.put(cf, 1);
            permutation.put((byte) (a | c | d | e | g), 2);
            permutation.put((byte) (a | c | d | f | g), 3);
            permutation.put((byte) bcdf, 4);
            permutation.put((byte) (a | b | d | f | g), 5);
            permutation.put((byte) (a | b | d | e | f | g), 6);
            permutation.put(acf, 7);
            permutation.put((byte) abcdefg, 8);
            permutation.put((byte) (a | b | c | d | f | g), 9);

            return permutation;
        }
    }
}
