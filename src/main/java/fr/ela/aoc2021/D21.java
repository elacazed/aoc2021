package fr.ela.aoc2021;

import java.util.Arrays;
import java.util.stream.IntStream;


public class D21 extends AoC {

    @Override
    public void run() {
        resolve("Test", 4, 8);
        resolve("Real", 2, 7);
    }

    private void resolve(String test, int ... startingPositions) {
        Game game = new Game(startingPositions);
        boolean winner = false;
        while (! winner) {
            winner = game.play();
        }
        System.out.println(test+" Score : "+game.score());
    }


    public class Game {
        int diceValue =0;
        int round = 0;
        int rolls = 0;

        int[] playersPositions;
        int[] playerScores;

        public Game(int ... playerPositions) {
            this.playersPositions = playerPositions;
            this.playerScores = new int[playersPositions.length];
            Arrays.fill(playerScores, 0);
        }

        public int rollDice() {
            if (diceValue == 100) {
                diceValue = 1;
            } else {
                diceValue +=1;
            }
            rolls++;
            return diceValue;
        }

        boolean play() {
            boolean winner = false;
            for (int player = 0; player < playersPositions.length; player++) {
                int[] dices = IntStream.of(1,2,3).map(i -> rollDice()).toArray();
                int advance = IntStream.of(dices).sum();
                playersPositions[player] = (playersPositions[player] + advance) % 10;
                playerScores[player] += (playersPositions[player] == 0 ? 10 : playersPositions[player]);
                 if (playerScores[player] >= 1000) {
                    winner = true;
                    break;
                }
            }
            round++;
            return winner;
        }


        int getLoserScore() {
            return IntStream.of(playerScores).min().orElseThrow();
        }

        long score() {
            return getLoserScore() * rolls;
        }
    }
}

