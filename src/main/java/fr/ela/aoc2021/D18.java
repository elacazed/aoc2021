package fr.ela.aoc2021;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;


public class D18 extends AoC {

    @Override
    public void run() {
        //SnailfishNumber sum = list(getTestInputPath(), this::parse).stream().reduce(new SnailfishNumber(), this::add);
        //reduce(sum);
        //System.out.println(sum);

        resolve(getTestInputPath(), "Test");
        resolve(getInputPath(), "Real");
    }

    public void resolve(Path input, String name) {
        List<SnailfishNumber> numbers = list(input, this::parse);
        SnailfishNumber sum = numbers.stream().reduce(new SnailfishNumber(), this::add);
        reduce(sum);
        System.out.println(name + " Final SnailNumber : " + sum);
        System.out.println(name + " Magnitude : " + sum.getMagnitude());
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

            int reg_idx = leftToRight.indexOf(this);
            if (reg_idx == -1) {
                throw new IllegalStateException(this + " not found in leftToRight list!");
            }

            // Ok, so we want the nearest regular numbers
            // to throw values on.  But the regular numbers
            // that are directly under this exploder are right next to it
            // so start the search at -2 and +2
            for (int i = reg_idx - 2; i >= 0; i--) {
                if (leftToRight.get(i).isRegularNumber()) {
                    leftToRight.get(i).value += left.value;
                    break;
                }
            }
            for (int i = reg_idx + 2; i < leftToRight.size(); i++) {
                if (leftToRight.get(i).isRegularNumber()) {
                    leftToRight.get(i).value += right.value;
                    break;
                }
            }
            left = null;
            right = null;
            value = 0;
        }

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


    public static SnailfishNumber findSplit(SnailfishNumber sn) {
        if (sn.isRegularNumber()) {
            return sn.value < 10 ? null : sn;
        }
        // Leftmost RegularNumber.
        SnailfishNumber left = findSplit(sn.left);
        return left == null ? findSplit(sn.right) : left;
    }

    public static SnailfishNumber findExploder(SnailfishNumber sn, int level) {
        if (sn.isRegularNumber()) {
            return null;
        }

        SnailfishNumber eleft = findExploder(sn.left, level + 1);
        if (eleft != null) {
            return eleft;
        }
        if (sn.mayExplode() && level >= 4) {
            return sn;
        }
        return findExploder(sn.right, level + 1);
    }

    public SnailfishNumber add(SnailfishNumber a, SnailfishNumber b) {
        SnailfishNumber result = new SnailfishNumber(a, b);
        reduce(result);
        return result;
    }

    public SnailfishNumber reduce(SnailfishNumber in) {
        while (true) {
            SnailfishNumber exp = findExploder(in, 0);
            if (exp != null) {
                exp.explode(in.leftToRight());
                continue;
            }
            SnailfishNumber split = findSplit(in);
            if (split != null) {
                split.split();
                continue;
            }
            // End of reduction!
            break;
        }
        return in;
    }


}

