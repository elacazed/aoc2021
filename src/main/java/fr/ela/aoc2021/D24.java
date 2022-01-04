package fr.ela.aoc2021;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D24 extends AoC {

    @Override
    public void run() {
        List<String> lines = list(getInputPath());
        List<Block> blocks = new ArrayList<>();
        int start = -1;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("inp")) {
                if (start == -1) {
                    start = i;
                } else {
                    blocks.add(new Block(start / 18, lines.subList(start + 1, i)));
                    start = i;
                }
            }
        }
        blocks.add(new Block((start + 1) / 18, lines.subList(start + 1, lines.size())));
        System.out.println("Found " + blocks.size() + " blocks");

        System.out.println("Plus grand nombre : "+solve(blocks, x -> Math.min(9, 9-x)));


        System.out.println("Plus petit nombre : "+solve(blocks, x -> Math.max(1, 1-x)));
    }


    public class Block implements BiConsumer<Integer, Monad> {

        private final List<String> list;

        private final int a;
        private final int b;
        private final int c;
        private final int order;

        public Block(int order, List<String> list) {
            this.list = list;
            this.order = order;
            a = Integer.parseInt(list.get(3).split(" ")[2]);
            b = Integer.parseInt(list.get(4).split(" ")[2]);
            c = Integer.parseInt(list.get(14).split(" ")[2]);
        }


        @Override
        public void accept(Integer input, Monad monad) {
            monad.input(input);
            list.forEach(monad::apply);
            System.out.println(order+" : "+monad);
        }
    }

    public String solve(List<Block> blocks, Function<Integer, Integer> minMax) {
        Stack<Block> stack = new Stack<>();
        int[] digits = new int[14];
        for (Block block : blocks) {
            if (block.a == 1) {
                stack.push(block);
            } else {
                Block prev = stack.pop();
                int c = prev.c + block.b;
                digits[prev.order] = minMax.apply(c);
                digits[block.order] = digits[prev.order] + c;
            }
        }
        Monad monad = Monad.test(digits, blocks);
        String input = IntStream.of(digits).mapToObj(Integer::toString).collect(Collectors.joining(""));
        System.out.println("Test with input "+input+" : "+monad+" "+(monad.get('x') == 0 ? "VALID":"INVALID"));
        return input;
    }


    static final Map<String, BiFunction<Integer, Integer, Integer>> functions = Map.of(
            "mul", (x, y) -> x * y,
            "add", Integer::sum,
            "mod", (x, y) -> x % y,
            "div", Math::floorDiv,
            "eql", (x, y) -> x.equals(y) ? 1 : 0);


    static class Monad {
        int[] values = {0, 0, 0, 0};

        public static Monad test(final int[] input, List<Block> instructions) {
            Monad m = new Monad();
            instructions.forEach(i -> i.accept(input[i.order], m));
            return m;
        }

        public void input(int input) {
            set('w', input);
        }

        void set(char c, int value) {
            values[c - 'w'] = value;
        }

        int get(char c) {
            return values[c - 'w'];
        }

        void apply(String command) {
            String[] elements = command.split(" ");
            char c = elements[1].charAt(0);
            set(c, functions.get(elements[0]).apply(get(c), getSecondOperand(elements[2])));
        }

        int getSecondOperand(String b) {
            if (b.length() == 1 && Character.isAlphabetic(b.charAt(0))) {
                return get(b.charAt(0));
            } else {
                return Integer.parseInt(b);
            }
        }

        public String toString() {
            return "w = " + values[0] + ", x = " + values[1] + ", y = " + values[2] + ", z = " + values[3];
        }
    }

}
