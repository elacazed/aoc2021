package fr.ela.aoc2021;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class D16 extends AoC {

    @Override
    public void run() {
        System.out.println("Test Versions Sum should be 16 : " + getVersionsSum("8A004A801A8002F478"));
        System.out.println("Test Versions Sum should be 12 : " + getVersionsSum("620080001611562C8802118E34"));
        System.out.println("Test Versions Sum should be 23 : " + getVersionsSum("C0015000016115A2E0802F182340"));
        System.out.println("Test Versions Sum should be 31 : " + getVersionsSum("A0016C880162017C3686B18A3D4780"));

        System.out.println("Real Versions Sum : " + getVersionsSum(readFile(getInputPath())));

        System.out.println("Test Versions value should be 3 : "+getValue("C200B40A82"));
        System.out.println("Test Versions value should be 54 : "+getValue("04005AC33890"));
        System.out.println("Test Versions value should be 7 : "+getValue("880086C3E88112"));
        System.out.println("Test Versions value should be 9 : "+getValue("CE00C43D881120"));
        System.out.println("Test Versions value should be 1 : "+getValue("D8005AC2A8F0"));
        System.out.println("Test Versions value should be 0 : "+getValue("F600BC2D8F"));
        System.out.println("Test Versions value should be 0 : "+getValue("9C005AC2F8F0"));
        System.out.println("Test Versions value should be 1 : "+getValue("9C0141080250320F1802104A08"));
        System.out.println("Real Versions value : " + getValue(readFile(getInputPath())));
    }

    public static long getValue(String packets) {
        return readPacket(toBits(packets), true).value();
    }
    public static int getVersionsSum(String packets) {
        return readPacket(toBits(packets), true).versionsSum();
    }

    public static String toBit(int hexa) {
        char[] result = new char[] {'0', '0','0','0'};
        char[] value = Integer.toString(Character.digit(hexa, 16), 2).toCharArray();
        System.arraycopy(value, 0, result, 4 - value.length, value.length);
        return new String(result);
    }

    public static String toBits(String hexa) {
        return hexa.chars().mapToObj(D16::toBit).collect(Collectors.joining(""));
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
            return new LitteralValue(version, typeId, bits.substring(6));
        } else {
            return new Operator(version, typeId, bits.substring(6));
        }
    }

    public static abstract class Packet {
        final int version;
        final int typeId;
        int length;

        public Packet(int version, int typeId, String rest) {
            this.version = version;
            this.typeId = typeId;
            length = 6;
            length += readData(rest);

        }

        abstract int readData(String data);

        int versionsSum() {
            return version;
        }

        public abstract long value();
    }

    public static class LitteralValue extends Packet {
        long value;

        public LitteralValue(int version, int typeId, String rest) {
            super(version, typeId, rest);
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

        public long value() {
            return value;
        }
    }

    public static class Operator extends Packet {
        private int lengthTypeId;

        List<Packet> subPackets;

        public Operator(int version, int typeId, String substring) {
            super(version, typeId, substring);
        }

        int versionsSum() {
            return version + subPackets.stream().mapToInt(Packet::versionsSum).sum();
        }

        @Override
        public long value() {
            switch (typeId) {
                case 0:
                    return subPackets.stream().mapToLong(Packet::value).sum();
                case 1:
                    return subPackets.stream().mapToLong(Packet::value).reduce(1, (x,y) -> x*y);
                case 2:
                    return subPackets.stream().mapToLong(Packet::value).min().orElseThrow();
                case 3:
                    return subPackets.stream().mapToLong(Packet::value).max().orElseThrow();
                case 5:
                    if (subPackets.size() > 2) {
                        throw new IllegalStateException();
                    }
                    return subPackets.get(0).value() > subPackets.get(1).value() ? 1 : 0;
                case 6:
                    if (subPackets.size() > 2) {
                        throw new IllegalStateException();
                    }
                    return subPackets.get(0).value() < subPackets.get(1).value() ? 1 : 0;
                case 7:
                    if (subPackets.size() > 2) {
                        throw new IllegalStateException();
                    }
                    return subPackets.get(0).value() == subPackets.get(1).value() ? 1 : 0;
            }
            throw new IllegalArgumentException(Integer.toString(typeId));
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

