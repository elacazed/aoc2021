package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class D12 extends AoC {

    private static final Pattern TUNNEL_PATTERN = Pattern.compile("([A-Za-z]+)-([A-Za-z]+)");

    @Override
    public void run() {
        process(getTestInputPath(), "Test");
        process(getInputPath(), "Real");
    }

    private void process(Path input, String name) {
        Cave start = new Cave("start");
        buildCaves(list(input), start);
        System.out.println(name + " Cave System is ready!");

        List<String> exitPaths = explore(start);
        System.out.println(name+" Cave System has "+exitPaths.size()+" exits paths");

    }


    public Map<String, Cave> buildCaves(List<String> lines, Cave start) {
        Map<String, Cave> map = new HashMap<>();
        map.put(start.name, start);
        String name1, name2;
        Cave cave1, cave2;
        for (String line : lines) {
            Matcher m = TUNNEL_PATTERN.matcher(line);
            if (m.matches()) {
                name1 = m.group(1);
                name2 = m.group(2);
                cave1 = map.computeIfAbsent(name1, Cave::new);
                cave2 = map.computeIfAbsent(name2, Cave::new);
                cave1.addNeighbour(cave2);
            }
        }

        return map;
    }


    public class Cave {
        final String name;
        final Set<Cave> neighbours;
        final boolean large;
        final boolean isStart;
        final boolean isEnd;

        Cave(String name) {
            this.name = name;
            this.isStart = "start".equals(name);
            this.isEnd = "end".equals(name);
            neighbours = new HashSet<>();
            large = Character.isUpperCase(name.charAt(0));
        }

        public void addNeighbour(Cave c) {
            this.neighbours.add(c);
            c.neighbours.add(this);
        }

        public String toString() {
            return name + " -> [" + neighbours.stream().map(c -> c.name).sorted().collect(Collectors.joining(",")) + "]";
        }

    }

    public List<String> explore(Cave start) {
        CaveVisitor stack = new CaveVisitor(start);
        return stack.explore();
    }



    public class CaveVisitor extends Stack<Cave> {

        public CaveVisitor(Cave start) {
            push(start);
        }

        @Override
        public Cave push(Cave item) {
            return super.push(item);
        }

        @Override
        public synchronized Cave pop() {
            Cave c = super.pop();
            return c;
        }

        public boolean canExplore(Cave cave) {
            return cave.large || ! contains(cave);
        }


        public String getPath() {
            return stream().map(c -> c.name).collect(Collectors.joining(","));
        }

        public List<String> explore() {
            List<String> paths = new ArrayList<>();
            Cave from = peek();
            for (Cave next : from.neighbours) {
                if (canExplore(next)) {
                    push(next);
                    if (next.isEnd) {
                        paths.add(getPath());
                    } else {
                        paths.addAll(explore());
                    }
                    pop();
                }
            }
            return paths;
        }
    }

}
