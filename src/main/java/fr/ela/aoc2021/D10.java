package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class D10 extends AoC {

    private static final Map<Character, Character> DELIMITERS = Map.of('(', ')', '[', ']', '{', '}', '<', '>');
    private static final Map<Character, Integer> SCORES = Map.of(')', 3, ']', 57, '}', 1197, '>', 25137);

    public static class Chunk {
        public final char opening;
        public final char closure;

        public final List<Chunk> children;
        boolean closed = false;
        boolean corrupted = false;

        public static Chunk open(char c) {
            if (isOpening(c)) {
                return new Chunk(c);
            } else {
                throw new IllegalArgumentException();
            }
        }

        private Chunk(char open) {
            this.opening = open;
            this.closure = DELIMITERS.get(open);
            this.children = new LinkedList<>();
        }

        public boolean close(char close) {
            corrupted = (close == closure);
            closed = true;
            return corrupted;
        }

        public static boolean isOpening(char c) {
            return DELIMITERS.containsKey(c);
        }

        public void addChild(Chunk newChunk) {
            this.children.add(newChunk);
        }
    }
    @Override
    public void run() {
        processPartOne(getTestInputPath(), "Test");
        processPartOne(getInputPath(), "Real");
    }

    public void processPartOne(Path input, String name) {
        List<String> lines = list(input);
        long score = lines.stream().mapToInt(this::syntaxErrorScore)
                .filter(i -> i > 0)
                .sum();

        System.out.println(name+" Syntax Error Score : "+score);
    }

    public int syntaxErrorScore(String line) {
        char[] chars = line.toCharArray();
        Stack<Chunk> chunks = new Stack<>();

        Chunk currentChunk = Chunk.open(chars[0]);
        int index = 1;
        while (index < chars.length) {
            char c = chars[index];
            if (Chunk.isOpening(c)) {
                chunks.push(currentChunk);
                Chunk newChunk = Chunk.open(c);
                currentChunk.addChild(newChunk);
                currentChunk = newChunk;
            } else {
                if (currentChunk.close(c)) {
                    if (chunks.isEmpty()) {
                        return -1;
                    }
                    currentChunk = chunks.pop();
                } else {
                    return SCORES.get(c);
                }
            }
            index++;
        }
        return 0;
    }

}
