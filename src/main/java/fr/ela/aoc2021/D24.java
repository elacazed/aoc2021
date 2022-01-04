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

/**
   14 blocs constitués comme suit :
   
   3 variables :
   ligne 4  : a tel que a = 1 ou 26
   Ligne 5  : b tel que b > 10 si a = 1, b < 0 si a = 26
   ligne 15 : c tel que 1 <= c <= 16
   
   Soit ce bloc le N+1 : en entrant dedans on a les valeurs xn, yn, zn
   lignes 1 et 8 => xn et yn ne rentrent pas en compte.
   1 <= w <= 9
   
   
   0  : inp w
   1  : mul x 0
        => x = 0
   2  : add x z
        => x = zn
   3  : mod x 26
   4  : div z a
        si si a = 1  => z = zn
        si si a = 26 => z = zn / 26
   5  : add x b
        si a = 1  => x = zn + b
        si a = 26 => x = (zn / 26) + b
   6  : eql x w
        si a = 1 b >= 10, x >= 10 => faux => x = 0
        si a = 26 : 0 ou 1
   7  : eql x 0
        si a = 1 : vrai => x = 1
        si a = 26 : 0 ou 1
   8  : mul y 0
        => y = 0
   9  : add y 25
        => y = 25
   10 : mul y x
        => y = 25x
   11 : add y 1
        => y = 25x + 1
   12 : mul z y
        => z = z*(25x + 1)
   13 : mul y 0
        => y = 0
   14 : add y w
        => y = w
   15 : add y c
        => y = w+c
   16 : mul y x
        => y = x * (w + c)
   17 : add z y
        => z = zn* (25x+1) + x*(w+c)
   
     |  1|  2|   3|  4|  5|  6|  7|  8|  9| 10| 11|  12|  13|  14
   --------------------------------------------------------------
   a |  1|  1|   1| 26|  1|  1| 26|  1| 26|  1| 26|  26|  26|  26
   b | 15| 14|  11|-13| 14| 15| -7| 10|-12| 15|-16|  -9|  -8|  -8
   c |  4| 16|  14|  3| 11| 13| 11|  7| 12| 15| 13|   1|  15|   4
   
   Quand a = 1, alors
       - ligne 4 : z = zn
       - b >= 10 => b+x > 10 (ligne 5) => x != w (ligne 6) => x = 1 ligne 7 =>
            z(fin) = 26 zn + w + c. avec w + c < 9+16 < 26.

           => On multiplie l'ancienne valeur z par 26 et on ajoute un nombre inférieur à 26.
           => le nombre ajouté est égal à w+c
   
   Quand a = 26 :
       - on copie dans x le modulo 26 (ie : la valeur ajoutée par le bloc "a=1" précédent
       - on divise par 26 => on retombe sur la valeur qu'on avait avant le bloc a=1 d'avant.
   
       Ensuite on ajoute b (qui est négatif, donc on a toujours qqchose < 26 dans x)
       - ligne 4 : x = w(n-1) + c (n-1)

   On construit une suite de nombres < 26 dont la valeur est modifiée par les 2 blocs "a=1" et "a=26" qui se correspondent.
   C'est une pile :
       SI a=1  => on ajoute une nouvelle valeur < 26 qui sera w+c dans la pile
       SI a=26 => on "dépile" (mod 26) et on modifie la valeur juste avant dans la pile (voir plus loin pour la valeur qu'on obtient)

   La pile contient les Zk, où k est la "profondeur" dans la pile, k va de 0 à 6 (14 blocs = 7 empilages et 7 dépilages)
   La valeur de Z à la fin est Somme(k=0, k=7) Zk

   Si on regarde le jeu de données, on peut faire correspondre les empilages et les dépilages.
   E     D
   --------
   3  <> 4
   6  <> 7
   8  <> 9
   10 <> 11
   5  <> 12
   2  <> 13
   1  <> 14

   A chaque entrée dans un bloc "D", la valeur de Z est w+c du bloc E correspondant
   
   avec  les blocs i et j, on peut jouer l'algo du bloc j (de type D) :
   Au départ, z = zi = le dernier z du bloc i (qui est de type E, donc wi+ci)
   
   Algo du bloc D correspondant :
   0  : inp wj
   1  : mul x 0    0, y, zi, wj
   2  : add x z    zi, y, zi, wj
   3  : mod x 26   zi, y, zi, wj
   4  : div z 26   zi, y, 0, wj           (zi = wi + ci, ci < 16 => zi < 26 => z = zi/26 = 0)
   5  : add x bj   zi+bj, y, 0, wj
   6  : eql x w    w4 == zi+bj ? 1 : 0, y, 0, wj
   7  : eql x 0    w4 == zi+bj ? 0 : 1, y, 0, wj
   8  : mul y 0    w4 == zi+bj ? 0 : 1, 0, 0, wj
   9  : add y 25   w4 == zi+bj ? 0 : 1, 25, 0, wj
   10 : mul y x    w4 == zi+bj ? 0 : 1, wj == zi+bj ? 0 : 25, 0, wj
   11 : add y 1    w4 == zi+bj ? 0 : 1, wj == zi+bj ? 1 : 26, 0, wj
   12 : mul z y    w4 == zi+bj ? 0 : 1, wj == zi+bj ? 1 : 26, 0, wj
   12 : mul z y    w4 == zi+bj ? 0 : 1, 0, 0, wj
   14 : add y w    w4 == zi+bj ? 0 : 1, wj, 0, wj
   15 : add y cj   w4 == zi+bj ? 0 : 1, wj+cj, 0, wj
   16 : mul y x    w4 == zi+bj ? 0 : 1, wj == zi+bj ? 0 : wj+cj, 0, wj
   17 : add z y    w4 == zi+bj ? 0 : 1, wj == zi+bj ? 0 : wj+cj, wj == zi+bj ? 0 : wj+cj, wj
   
   On veut Z final = 0 pour mettre à 0 tous les Z contenus dans la pile, donc wj = zi+bj = wi + ci + bj

   - Part 1 : les valeurs max de wi et wj (wi "max en premier" parce qu'il a un poids plus fort)
   - Part 2 : les valeurs min de wi et wj (wi "min en premier" parce qu'il a un poids plus fort)
       telles que :
           wj - wi = ci + bj
           0 < wi <= 9
           0 < wj <= 9
   
       => wi <= 9 (max wj) - (ci+bj) => wi = Math.min(9, 9-(ci+bj)) et wj = wi + (ci+bj)
       => wi >= 1 (min wj) - (ci+bj) => wi = Math.max(1, 1-(ci+bj)) et wj = wi + (ci+bj)
   
   Donc :
   
   E     D | ci+bj | Di| Dj| di| dj
   --------|-------|---|---|---|---|
   3  <>  4|  1    | 8 | 9 | 1 | 2 |
   6  <>  7|  6    | 3 | 9 | 1 | 7 |
   8  <>  9| -5    | 9 | 4 | 6 | 1 |
   10 <> 11| -1    | 9 | 8 | 2 | 1 |
   5  <> 12|  2    | 7 | 9 | 1 | 3 |
   2  <> 13|  8    | 1 | 9 | 1 | 9 |
   1  <> 14| -4    | 9 | 5 | 5 | 1 |
   
   => Part 1 : 91897399498995
   => Part 2 : 51121176121391
 **/
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

        System.out.println("Plus grand nombre : " + solve(blocks, x -> Math.min(9, 9 - x)));


        System.out.println("Plus petit nombre : " + solve(blocks, x -> Math.max(1, 1 - x)));
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
            System.out.println(order + " : " + monad);
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
        System.out.println("Test with input " + input + " : " + monad + " " + (monad.get('x') == 0 ? "VALID" : "INVALID"));
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
