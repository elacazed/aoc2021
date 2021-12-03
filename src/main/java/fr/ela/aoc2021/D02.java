package fr.ela.aoc2021;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class D02 extends AoC {

    public void run() {
        Consumer<Position> testTrip = trip(stream(getTestInputPath()));
        Consumer<Position> realTrip = trip(stream(getInputPath()));
        System.out.println("Test Result : " + new Position().tripResult(testTrip));
        System.out.println("Test Real : " + new Position().tripResult(realTrip));
        System.out.println("Test Result 2 : " + new AimingPosition().tripResult(testTrip));
        System.out.println("Test Real 2 : " + new AimingPosition().tripResult(realTrip));
    }

    public static Consumer<Position> trip(Stream<String> input) {
        return input
                .map(Direction::toConsumer)
                .reduce(Consumer::andThen)
                .orElseThrow();
    }

    private static class Position {
        int posX, posY, aim = 0;
        int tripResult(Consumer<Position> trip) {trip.accept(this); return posX*posY;}
        void down(int i) { posY +=i;}
        void forward(int i) { posX +=i; posY += aim * i;}
    }

    private static class AimingPosition extends Position {
        void down(int i) { aim += i;}
    }

    public enum Direction {
        forward(Position::forward),
        up((p,i) -> p.down(-1*i)),
        down(Position::down);
        final BiConsumer<Position, Integer> moveFunction;
        Direction(BiConsumer<Position, Integer> moveFunction) {
            this.moveFunction = moveFunction;
        }
        private static Consumer<Position> toConsumer(String command) {
            String[] values = command.split(" ");
            return pos -> Direction.valueOf(values[0]).moveFunction.accept(pos, Integer.parseInt(values[1]));
        }
    }
}
