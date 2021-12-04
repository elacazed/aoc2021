package fr.ela.aoc2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class D04 extends AoC {
    @Override
    public void run() {
        Game test = new Game(list(getTestInputPath()));
        Board testWinner = test.getFirstWinner();
        System.out.println("Test Score part 1 : "+testWinner.score());
        Board lastTestWinner = test.getLastWinner();
        System.out.println("Test Score part 2 : "+lastTestWinner.score());

        Game real = new Game(list(getInputPath()));
        Board realWinner = real.getFirstWinner();
        System.out.println("Real Score part 1 : "+realWinner.score());
        Board lastWinner = real.getLastWinner();
        System.out.println("Real Score part 2 : "+lastWinner.score());
    }

    private static class Game {
        final LinkedList<Integer> numbers;
        final List<Board> boards;
        final LinkedList<Board> winnerBoards = new LinkedList<>();

        public Game(List<String> list) {
            numbers = Arrays.stream(list.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toCollection(LinkedList::new));
            boards = new LinkedList<>();
            for (int i = 2; i < list.size(); i += 5) {
                boards.add(new Board(list.subList(i, i + 5)));
                i++;
            }
        }

        public void play(int number) {
            Map<Boolean, List<Board>> map = boards.stream().collect(Collectors.groupingBy(b -> b.mark(number)));
            var wins = map.get(Boolean.TRUE);
            if (wins != null) {
                winnerBoards.addAll(wins);
                boards.removeAll(wins);
            }
        }

        public Board getFirstWinner() {
            int number = -1;
            while (winnerBoards.isEmpty() && !numbers.isEmpty()) {
                number = numbers.removeFirst();
                play(number);
            }
            if (winnerBoards.size() > 1) {
                throw new RuntimeException(winnerBoards.size()+" boards win!");
            } else {
                return winnerBoards.get(0);
            }
        }

        public Board getLastWinner() {
            while (! numbers.isEmpty()) {
                play(numbers.removeFirst());
            }
            return winnerBoards.getLast();
        }

    }

    private record Position(int row, int col) {
    }

    private static class Board {
        private final Map<Integer, Position> positions;
        private final int[] colsSum, rowsSum;
        private int score;

        public Board(List<String> lines) {
            int size = 5;
            positions = new HashMap<>();
            rowsSum = new int[size];
            colsSum = new int[size];

            for (int row = 0; row < size; row++) {
                String line = lines.get(row);
                for (int col = 0; col < size; col++) {
                    int value = Integer.parseInt(line.substring(3*col, 3*(col+1)-1).trim());
                    positions.put(value,new Position(row, col));
                    rowsSum[row] += value;
                    colsSum[col] += value;
                }
            }
        }

        int score() {
            return score;
        }

        public boolean mark(int number) {
            Position pos = positions.get(number);
            if (pos == null) {
                return false;
            }
            colsSum[pos.col] -= number;
            rowsSum[pos.row] -= number;
            boolean win = (colsSum[pos.col] == 0 || rowsSum[pos.row] == 0);
            if (win) {
                score = Arrays.stream(colsSum).sum() * number;
            }
            return win;
        }


    }

}
