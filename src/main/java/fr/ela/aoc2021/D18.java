package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class D18 extends AoC {

    @Override
    public void run() {
        resolve(getTestInputPath(), "Test");
        resolve(getInputPath(), "Real");
    }

    public void resolve(Path input, String name) {
        List<SnailfishNumber> numbers = list(input, this::parse);
        SnailfishNumber sum = numbers.stream().reduce(new SnailfishNumber(), this::add).reduce().reduce();

        System.out.println(name + " Final SnailNumber : " + sum);
        System.out.println(name + " Magnitude : " + sum.getMagnitude());

        // Les nombres sont modifiés à chaque opération (explode / split) donc il faut repartir du début..
        // La liste initiale n'a pas été modifiée : on copie les SnailfishNumbers dans add(..)
        long magnitudeMax = 0;
        for (int i = 0; i < numbers.size(); i++) {
            for (int j = 0; j < numbers.size(); j++) {
                if (i != j) {
                    SnailfishNumber sn1 = numbers.get(i);
                    SnailfishNumber sn2 = numbers.get(j);

                    magnitudeMax = Math.max(add(sn1, sn2).reduce().getMagnitude(), magnitudeMax);
                }
            }
        }
        System.out.println(name + " Maximum Magnitude : " + magnitudeMax);
    }


    public SnailfishNumber parse(String line) {
        char[] chars = line.toCharArray();
        Stack<SnailfishNumber> stack = new Stack();
        int index = 1;
        SnailfishNumber number;
        stack.push(new SnailfishNumber());

        while (index < chars.length) {
            number = stack.peek();
            char c = chars[index];
            if (c == '[') {
                SnailfishNumber sn = new SnailfishNumber();
                number.addElement(sn);
                stack.push(sn);
            }
            if (c == ']') {
                SnailfishNumber closed = stack.pop();
                if (stack.isEmpty()) {
                    return closed;
                }
            }
            if (Character.isDigit(c)) {
                number.addElement(regularNumber(Character.getNumericValue(c)));
            }
            index++;
        }
        return stack.firstElement();
    }

    static SnailfishNumber regularNumber(int value) {
        SnailfishNumber sn = new SnailfishNumber();
        sn.value = value;
        return sn;
    }

    public static class SnailfishNumber {
        SnailfishNumber left;
        SnailfishNumber right;
        int value;

        public SnailfishNumber() {

        }

        public SnailfishNumber(SnailfishNumber left, SnailfishNumber right) {
            this.left = left;
            this.right = right;
        }

        boolean isRegularNumber() {
            return left == null;
        }

        boolean isPair() {
            return left != null;
        }

        public String toString() {
            if (isRegularNumber()) {
                return Integer.toString(value);
            }
            return String.format("[%s,%s]", left.toString(), right.toString());
        }

        public void addElement(SnailfishNumber sn) {
            if (left == null) {
                left = sn;
            } else if (right == null) {
                right = sn;
            } else {
                throw new IllegalStateException();
            }
        }

        public boolean mayExplode() {
            return isPair() && left.isRegularNumber() && right.isRegularNumber();
        }

        public long getMagnitude() {
            return isRegularNumber() ? value : 3L * left.getMagnitude() + 2L * right.getMagnitude();
        }

        public List<SnailfishNumber> leftToRight() {
            List<SnailfishNumber> res = new ArrayList<>();
            getList(this, res);
            return res;
        }

        public static void getList(SnailfishNumber in, List<SnailfishNumber> lst) {
            if (in.isRegularNumber()) {
                lst.add(in);
            } else {
                getList(in.left, lst);
                lst.add(in);
                getList(in.right, lst);
            }
        }

        public void split() {
            left = new SnailfishNumber();
            right = new SnailfishNumber();
            left.value = value / 2;
            right.value = (value % 2 == 1) ? left.value + 1 : left.value;
            value = 0;
        }

        private void explode(List<SnailfishNumber> leftToRight) {
            if (left.isPair() || right.isPair()) {
                throw new IllegalStateException("Cannot explode " + this);
            }
            int expIndex = leftToRight.indexOf(this);
            if (expIndex == -1) {
                throw new IllegalStateException(this + " not found in leftToRight list!");
            }
            // Dans la liste, les éléments d'une paire qui explose sont juste à côté de la paire.
            // Donc on doit commencer à chercher à index -2 pour la gauhe, et index +2 pour la droite.

            for (int i = expIndex - 2; i >= 0; i--) {
                if (leftToRight.get(i).isRegularNumber()) {
                    leftToRight.get(i).value += left.value;
                    break;
                }
            }
            for (int i = expIndex + 2; i < leftToRight.size(); i++) {
                if (leftToRight.get(i).isRegularNumber()) {
                    leftToRight.get(i).value += right.value;
                    break;
                }
            }
            left = null;
            right = null;
            value = 0;
        }

        public SnailfishNumber reduce() {
            while (true) {
                SnailfishNumber exp = findExploder(0);
                if (exp == null) {
                    SnailfishNumber split = findSplit();
                    if (split == null) {
                        return this;
                    }
                    split.split();
                } else {
                    exp.explode(this.leftToRight());
                }
            }
        }

        public SnailfishNumber findSplit() {
            if (isRegularNumber()) {
                return value < 10 ? null : this;
            }
            // Leftmost RegularNumber.
            SnailfishNumber left = this.left.findSplit();
            return left == null ? this.right.findSplit() : left;
        }

        public SnailfishNumber findExploder(int level) {
            if (isRegularNumber()) {
                return null;
            }

            SnailfishNumber eleft = left.findExploder(level + 1);
            if (eleft != null) {
                return eleft;
            }
            if (mayExplode() && level >= 4) {
                return this;
            }
            return right.findExploder(level + 1);
        }

        public SnailfishNumber copy() {
            SnailfishNumber sn = new SnailfishNumber();
            sn.left = left == null ? null : left.copy();
            sn.right = right == null ? null : right.copy();
            sn.value = value;
            return sn;
        }
    }



    public SnailfishNumber add(SnailfishNumber left, SnailfishNumber right) {
        return new SnailfishNumber(left.copy(), right.copy()).reduce();
    }


}

