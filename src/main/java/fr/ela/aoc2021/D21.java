package fr.ela.aoc2021;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;


public class D21 extends AoC {

    @Override
    public void run() {
        resolve("Test", 4, 8);
        resolve("Real", 2, 7);
    }

    private void resolve(String test, int p1, int p2) {
        Game game = new Game(p1, p2);
        boolean winner = false;
        while (!winner) {
            winner = game.play();
        }
        System.out.println(test + " Score : " + game.score());

        WinCount count = count(p1, p2);
        System.out.println(test + " WinCount : " + Math.max(count.p1, count.p2));
    }


    public class Game {
        int diceValue = 0;
        int round = 0;
        int rolls = 0;

        int[] pos;
        int[] scores = {0, 0};

        public Game(int player1, int player2) {
            this.pos = new int[2];
            pos[0] = player1;
            pos[1] = player2;
        }

        public int rollDice() {
            if (diceValue == 100) {
                diceValue = 1;
            } else {
                diceValue += 1;
            }
            rolls++;
            return diceValue;
        }

        boolean play() {
            boolean winner = false;
            for (int player = 0; player < pos.length; player++) {
                int[] dices = IntStream.of(1, 2, 3).map(i -> rollDice()).toArray();
                int advance = IntStream.of(dices).sum();
                pos[player] = (pos[player] + advance) % 10;
                scores[player] += (pos[player] == 0 ? 10 : pos[player]);
                if (scores[player] >= 1000) {
                    winner = true;
                    break;
                }
            }
            round++;
            return winner;
        }


        int getLoserScore() {
            return IntStream.of(scores).min().orElseThrow();
        }

        long score() {
            return getLoserScore() * rolls;
        }
    }

    // On se moque des tirages, on veut juste la somme obtenue, et le nombre d'univers dans lesquels ça arrive
    // PArtant d'une situation donnée, on va toujours arriver dans les même univers après n'importe quel tirage d'un groupe donné.
    public Play[] plays = {
            new Play(3, 1), //1, 1, 1),
            new Play(4, 3),//[1, 1, 2] [1, 2, 1] [2, 1, 1]
            new Play(5, 6), //[1, 1, 3] [1, 2, 2] [1, 3, 1] [2, 1, 2] [2, 2, 1] [3, 1, 1]
            new Play(6, 7), //[1, 2, 3] [1, 3, 2], [2, 1, 3], [2, 3, 1] [3, 1, 2] [3, 2, 1] [2, 2, 2]
            new Play(7, 6),//[1, 3, 3] [3, 2, 2] [2, 2, 3] [2, 3, 2] [3, 1, 3] [3, 3, 1]
            new Play(8, 3), //[2, 3, 3] [3, 2, 3] [3, 3, 2]
            new Play(9, 1)//[3, 3, 3]
    };

    record Play(int sum, int occurences) {
        int newPos(int p) {
            return ((p + sum - 1) % 10) + 1;
        }
    }

    record WinCount(long p1, long p2) {
        WinCount add(int occurences, long p1, long p2) {
            return new WinCount(this.p1 + (occurences * p1), this.p2 + (occurences * p2));
        }
    }

    private Map<String, WinCount> gameSituationCache = new HashMap<>();

    WinCount countWins(int p1, int s1, int p2, int s2) {
        if (s2 >= 21) {
            return new WinCount(0, 1);
        }
        String gameSituationKey = p1 + "," + s1 + "," + p2 + "," + s2;
        WinCount wc = gameSituationCache.get(gameSituationKey);
        if (wc != null) {
            return wc;
        }
        WinCount res = new WinCount(0, 0);
        for (Play play : plays) {
            int np1 = play.newPos(p1);
            int ns1 = s1 + np1;
            wc = countWins(p2, s2, np1, ns1);
            res = res.add(play.occurences, wc.p2, wc.p1);
        }
        gameSituationCache.put(gameSituationKey, res);
        return res;
    }

    WinCount count(int p1, int p2) {
        return countWins(p1, 0, p2, 0);
    }


}

