package fr.ela.aoc2021;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        System.out.println("Test result part 2 : " + testEntries.stream().mapToInt(Entry::decodeValue).sum());
        System.out.println("Real result part 2 : " + entries.stream().mapToInt(Entry::decodeValue).sum());

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


        public int decodeValue() {
            DigitDictionary dict = new DigitDictionary(inputs);
            int size = outputs.size();
            int result = 0;
            for (int i = 0; i < size; i++) {
                result += Math.pow(10, i) * dict.value(outputs.get(size - 1 - i));
            }
            System.out.println(inputs.stream().collect(Collectors.joining(" "))+" | "+outputs.stream().collect(Collectors.joining(" "))+" : "+result);
            return result;
        }
    }


    private class DigitDictionary {

        private final HashMap<String, Integer> map = new HashMap<>();

        public DigitDictionary(List<String> entries) {
            // On a les 10 chiffres dans les inputs. Donc si on trie les inputs par longueur on sait où sont 1, 7, 4, et 8.
            Map<Integer, String> intToCode = new HashMap<>();

            entries.sort(Comparator.comparingInt(String::length));

            String one = entries.get(0);
            String seven = entries.get(1);
            String four = entries.get(2);
            String eight = entries.get(9);
            String zero = null, two = null, three = null, five = null, six = null, nine = null;

            // On a 3 codes de 6 caractères aux index 6,7,8 pour les caractères 0, 6 et 9
            // Les codes de 0 et 9 contiennent les caractères de 1 et 7, mais pas 6 (il manque la barre en haut à droite) => on connaît 6.
            // Le code de 9 contient tous les caractères du cide de 4, mais pas 0 => on peut distinguer 0 et 9.
            for (int i = 6; i <= 8; i++) {
                String d = entries.get(i);
                if (matchesAll(d, one, seven)) {
                    if (matchesAll(d, four)) {
                        nine = d;
                    } else {
                        zero = d;
                    }
                } else {
                    six = d;
                }
            }
            // On a 3 chaînes de longueur 5 pour les caractères 2, 3 et 5.
            // Les codes de 2 et 5 ne contiennent pas tous les caractères du code de 1 => on connaît 3.
            // Le code de 6 contient tous les caractères du code de 5.
            for (int i = 3; i <= 5; i++) {
                String d = entries.get(i);
                if (! matchesAll(d, one)) {
                    if (matchesAll(six, d)) {
                        five = d;
                    } else {
                        two = d;
                    }
                } else {
                    three = d;
                }
            }
            map.put(zero, 0);
            map.put(one, 1);
            map.put(two, 2);
            map.put(three, 3);
            map.put(four, 4);
            map.put(five, 5);
            map.put(six, 6);
            map.put(seven, 7);
            map.put(eight, 8);
            map.put(nine, 9);
        }

        /**
         * match si toutes le code à l'index i contient toutes les lettres du code à l'index j.
         */
        private boolean matchesAll(String unknownCode, String... codes) {
            for (String code : codes) {
                for (char c : code.toCharArray()) {
                    if (unknownCode.indexOf(c) == -1) {
                        return false;
                    }
                }
            }
            return true;
        }

        public int value(String code) {
            return map.get(code);
        }
    }

}
