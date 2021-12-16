package fr.ela.aoc2021;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class D16 extends AoC {


    private static Map<Character, String> codes = buildMap();

    private static Map<Character, String> buildMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('0', "0000");
        map.put('1', "0001");
        map.put('2', "0010");
        map.put('3', "0011");
        map.put('4', "0100");
        map.put('5', "0101");
        map.put('6', "0110");
        map.put('7', "0111");
        map.put('8', "1000");
        map.put('9', "1001");
        map.put('A', "1010");
        map.put('B', "1011");
        map.put('C', "1100");
        map.put('D', "1101");
        map.put('E', "1110");
        map.put('F', "1111");
        return map;
    }

    @Override
    public void run() {
        System.out.println(toBits(readFile(getInputPath())));
        System.out.println("Test Versions Sum should be 16 : " + getVersionsSum("8A004A801A8002F478"));
        System.out.println("Test Versions Sum should be 12 : " + getVersionsSum("620080001611562C8802118E34"));
        System.out.println("Test Versions Sum should be 23 : " + getVersionsSum("C0015000016115A2E0802F182340"));
        System.out.println("Test Versions Sum should be 31 : " + getVersionsSum("A0016C880162017C3686B18A3D4780"));

        System.out.println("Real Versions Sum : " + getVersionsSum(readFile(getInputPath())));

    }


    public static int getVersionsSum(String packets) {
        return readPacket(toBits(packets), true).versionsSum();
    }

    public static String toBits(String hexa) {
        return hexa.chars().mapToObj(c -> codes.get((char) c)).collect(Collectors.joining(""));
    }

    public static List<Packet> readPackets(String bits, boolean pad) {
        String remaining = bits;
        List<Packet> packets = new ArrayList<>();
        while (remaining.length() > 0) {
            Packet p = readPacket(remaining, pad);
            remaining = remaining.substring(p.length);
            packets.add(p);
        }
        return packets;
    }


    public static Packet readPacket(String bits, boolean pad) {

        int version = Integer.parseInt(bits.substring(0, 3), 2);
        int typeId = Integer.parseInt(bits.substring(3, 6), 2);

        if (typeId == 4) {
            return new LitteralValue(version, typeId, bits.substring(6), pad);
        } else {
            return new Operator(version, typeId, bits.substring(6), pad);
        }
    }

    public static class Packet {
        final int version;
        final int typeId;
        int length;

        public Packet(int version, int typeId, String rest, boolean pad) {
            this.version = version;
            this.typeId = typeId;
            length = 6;
            length += readData(rest);
            if (pad) {
                if (length % 4 != 0) {
                    length += 4 - (length % 4);
                }
            }
        }

        int readData(String data) {
            return 0;
        }

        int versionsSum() {
            return version;
        }
    }

    public static class LitteralValue extends Packet {
        long value;

        public LitteralValue(int version, int typeId, String rest, boolean pad) {
            super(version, typeId, rest, pad);
        }

        @Override
        public int readData(String rest) {
            int i = 0;
            StringBuilder sb = new StringBuilder();
            while (rest.charAt(i) == '1') {
                sb.append(rest, i + 1, i + 5);
                i += 5;
            }
            sb.append(rest, i + 1, i + 5);
            value = Long.parseLong(sb.toString(), 2);
            return i + 5;
        }
    }

    public static class Operator extends Packet {
        int lengthTypeId;

        List<Packet> subPackets;

        public Operator(int version, int typeId, String substring, boolean pad) {
            super(version, typeId, substring, pad);
        }

        int versionsSum() {
            return version + subPackets.stream().mapToInt(Packet::versionsSum).sum();
        }

        public int readData(String rest) {
            subPackets = new ArrayList<>();
            lengthTypeId = Character.getNumericValue(rest.charAt(0));
            int start = 1;
            if (lengthTypeId == 0) {
                start += 15;
                int end = start + Integer.parseInt(rest.substring(1, 16), 2);
                subPackets.addAll(readPackets(rest.substring(16, end), false));
                return end;
            } else {
                start += 11;
                int nbSubPackets = Integer.parseInt(rest.substring(1, 12), 2);
                for (int i = 0; i < nbSubPackets; i++) {
                    Packet p = readPacket(rest.substring(start), false);
                    subPackets.add(p);
                    start = start + p.length;
                }
            }
            return start;
        }
    }

}
