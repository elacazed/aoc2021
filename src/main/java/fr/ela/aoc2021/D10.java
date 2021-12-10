package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D10 extends AoC {

    private static final Map<Character, Character> DELIMITERS = Map.of('(', ')', '[', ']', '{', '}', '<', '>');
    private static final Map<Character, Integer> ERROR_SCORES = Map.of(')', 3, ']', 57, '}', 1197, '>', 25137);
    private static final Map<Character, Integer> AUTOCOMPLETE_SCORES = Map.of(')', 1, ']', 2, '}', 3, '>', 4);

    public static class Chunk {
        public final char opening;
        public final char expectedClosure;
        public char closure;

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
            this.expectedClosure = DELIMITERS.get(open);
            this.children = new LinkedList<>();
        }

        public boolean close(char close) {
            corrupted = (close != expectedClosure);
            closed = true;
            closure = close;
            return !corrupted;
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
        process(getTestInputPath(), "Test");
        process(getInputPath(), "Real");
    }

    public void process(Path input, String name) {
        Map<Boolean, List<Stack<Chunk>>> chunksList = list(input, this::readLine).stream().collect(Collectors.groupingBy(this::isCorrupted));

        long score = chunksList.get(Boolean.TRUE).stream().mapToInt(this::syntaxErrorCode).sum();
        System.out.println(name + " Syntax Error Score : " + score);
        List<Stack<Chunk>> incompleteChunks = chunksList.get(Boolean.FALSE);
        System.out.println(name + " Autocomplete Score : " + autocompleteScore(incompleteChunks));;

    }

    public boolean isCorrupted(Stack<Chunk> chunks) {
        return chunks.peek().corrupted;
    }

    public int syntaxErrorCode(Stack<Chunk> chunks) {
        return ERROR_SCORES.get(chunks.peek().closure);
    }

    public long autocompleteScore(List<Stack<Chunk>> chunksList) {
        List<Long> scores = chunksList.stream().mapToLong(this::autocomplete).boxed().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        return scores.get(scores.size() / 2);
    }

    public long autocomplete(Stack<Chunk> chunks) {
        long score = 0L;
        int i = 0;
        while (!chunks.isEmpty()) {
            score = score * 5 + AUTOCOMPLETE_SCORES.get(chunks.pop().expectedClosure);
            i++;
        }
        return score;
    }


    public Stack<Chunk> readLine(String line) {
        char[] chars = line.toCharArray();
        Stack<Chunk> chunks = new Stack<>();

        List<Chunk> completeChunks = new ArrayList<>();

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
                        completeChunks.add(currentChunk);
                        if (index != chars.length - 1) {
                            index++;
                            currentChunk = Chunk.open(chars[index]);
                        }
                    } else {
                        currentChunk = chunks.pop();
                    }
                } else {
                    if (chunks.contains(currentChunk)) {
                        while (chunks.peek() != currentChunk) {
                            chunks.pop();
                        }
                    } else {
                        chunks.push(currentChunk);
                    }
                    return chunks;
                }
            }
            index++;
        }
        if (!chunks.contains(currentChunk)) {
            chunks.push(currentChunk);
        }
        return chunks;
    }

}
