package vu.mif.ingvaras.galinskas.math;

import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class MatrixOperations {

    public static ArrayList<Boolean> computePartialCGuess(ArrayList<Boolean> a, ArrayList<Boolean> b) {
        ArrayList<Boolean> L = (ArrayList<Boolean>) a.clone();
        for (int i = 0; i < L.size(); i++) {
            if (L.get(i).equals(Boolean.TRUE)) {
                if(b.get(0).equals(Boolean.FALSE)) {
                    L.set(i, Boolean.FALSE);
                }
                b.remove(0);
            }
        }
        return L;
    }

    public static long commonPositions(long hammingWeight, long t) {
        return t - hammingWeight / 2;
    }

    public static long hammingWeight(ArrayList<Boolean> a) {
        return a.stream().filter(e -> e == Boolean.TRUE).count();
    }

    public static GF2Vector computeMessageRepresentative(byte[] var1, int k) {
        int maxLength = k >> 3;
        byte[] var2 = new byte[maxLength + ((k & 7) != 0 ? 1 : 0)];
        System.arraycopy(var1, 0, var2, 0, var1.length);
        var2[var1.length] = 1;
        return GF2Vector.OS2VP(k, var2);
    }

    public static String byteArrayToBits(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(Integer.toBinaryString(b & 255 | 256).substring(1));
        }
        return builder.toString();
    }

    public static byte[] bitsToByteArray(String bits) {
        byte[] bytes = new BigInteger(bits, 2).toByteArray();
        if(bytes[0] == 0) {
            return Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        return bytes;
    }

    public static String trimString(String string) {
        return string.replaceAll("\n[0-9]+: ", "").replaceAll("0: ", "").replaceAll(" ", "");
    }

    public static ArrayList<Boolean> addVectors(ArrayList<Boolean> a, ArrayList<Boolean> b) {
        if(a.size() != b.size())
            throw new ArithmeticException();
        ArrayList<Boolean> result = new ArrayList<>();
        for(int i = 0; i < a.size(); i++) {
            result.add(Boolean.logicalXor(a.get(i), b.get(i)));
        }
        return result;
    }

    public static ArrayList<Boolean> toLittleEndian(ArrayList<Boolean> a) {
        ArrayList<Boolean> b = new ArrayList<>();
        for(int i = 0; i < a.size(); i+=8) {
            for(int j = i + 7; j >= i; j--) {
                b.add(a.get(j));
            }
        }
        return b;
    }

    public static String convertToString(ArrayList<Boolean> a) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < a.size(); i++) {
            if(a.get(i))
                builder.append('1');
            else
                builder.append('0');
        }
        return builder.toString();
    }
}
