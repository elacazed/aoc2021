package fr.ela.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class d02 {

    public static void main(String[] args) {
        try {
            System.out.println("Test Result : " + computePosition(InputFileUtil.getPath(d02.class, "input-test"), new Position()));
            System.out.println("Test Real : " + computePosition(InputFileUtil.getPath(d02.class, "input"), new Position()));

            System.out.println("Test Result 2 : " + computePosition(InputFileUtil.getPath(d02.class, "input-test"), new AimingPosition()));
            System.out.println("Test Real 2 : " + computePosition(InputFileUtil.getPath(d02.class, "input"), new AimingPosition()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int computePosition(Path input, Position pos) throws IOException {
        return Files.lines(input)
                .reduce(pos, move, (p1, p2) -> p1).result();
    }

    private static final BiFunction<Position, String, Position> move = (p, s) -> {
            String[] values = s.split(" ");
            Direction.valueOf(values[0]).moveFunction.accept(p, Integer.parseInt(values[1]));
            return p;
        };

    private static class Position {
        int posX = 0;
        int posY = 0;
        int result() {return posX*posY;}
        void down(int i) { posY +=i;}
        void up(int i) { down(-1*i);}
        void forward(int i) { posX +=i;}
    }

    private static class AimingPosition extends Position {
        int realY = 0;
        void forward(int i) { posX +=i; realY += i*posY;}
        int result() {return posX*realY;}
    }

    public enum Direction {
        forward(Position::forward),
        up(Position::up),
        down(Position::down);

        final BiConsumer<Position, Integer> moveFunction;

        Direction(BiConsumer<Position, Integer> moveFunction) {
            this.moveFunction = moveFunction;
        }
    }

}
