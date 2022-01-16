package vu.mif.ingvaras.galinskas;

import com.google.common.base.Stopwatch;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.pqc.crypto.mceliece.*;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        for(int t = 1; t <= 7; t++) {
            McElieceParameters params = new McElieceParameters(6, t);
            McElieceKeyGenerationParameters genParams = new McElieceKeyGenerationParameters(new SecureRandom(), params);

            McElieceKeyPairGenerator keyGeneration = new McElieceKeyPairGenerator();
            keyGeneration.init(genParams);
            AsymmetricCipherKeyPair keyPair = keyGeneration.generateKeyPair();

            McEliecePublicKeyParameters publicKey = (McEliecePublicKeyParameters) keyPair.getPublic();

            System.out.println("n: " + publicKey.getN());
            System.out.println("k: " + publicKey.getK());
            System.out.println("t: " + publicKey.getT());

            McElieceCipher encrypt = new McElieceCipher();
            encrypt.init(true, publicKey);

            byte[] message = {12};
            byte[] cipher = encrypt.messageEncrypt(message);

            System.out.println("message: " + trimString(computeMessageRepresentative(message, publicKey.getK()).toString()));
            System.out.println("cipher: " + convertToString(toLittleEndian(BinaryMatrix.createBinaryVector(byteArrayToBits(cipher)))));

            Stopwatch stopwatch = Stopwatch.createStarted();

            ErrorVectorPermutationGenerator generator = new ErrorVectorPermutationGenerator(publicKey.getT(), publicKey.getN());

            ArrayList<Boolean> m = BinaryMatrix.createBinaryVector(trimString(computeMessageRepresentative(message, publicKey.getK()).toString()));
            ArrayList<Boolean> c = toLittleEndian(BinaryMatrix.createBinaryVector(byteArrayToBits(cipher)));
            BinaryMatrix G = new BinaryMatrix(publicKey.getN(), publicKey.getK(), trimString(publicKey.getG().computeTranspose().toString()));

            while (generator.hasMore()) {
                ArrayList<Boolean> cGuess = addVectors(generator.nextPermutation(), c);
                G.appendOneColumn(cGuess);
                ArrayList<Boolean> mGuess = LinearAlgebraSolver.solveEquation(G);
                if (m.equals(mGuess)) {
                    System.out.println("Success");
                    stopwatch.stop();
                    System.out.println("Time elapsed: "+ stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms\n");
                    break;
                }
                G = new BinaryMatrix(publicKey.getN(), publicKey.getK(), trimString(publicKey.getG().computeTranspose().toString()));
            }
        }
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
