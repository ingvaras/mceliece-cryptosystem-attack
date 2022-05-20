package vu.mif.ingvaras.galinskas.attacks;

import com.google.common.base.Stopwatch;
import lombok.Builder;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCipher;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import vu.mif.ingvaras.galinskas.math.MatrixOperations;

import java.util.ArrayList;

@Builder
public class ReceiverResponseAttack implements CipherAttack {

    private final int k;
    private final int n;
    private final int t;
    private final GF2Matrix G;
    private final McElieceCipher decrypt;
    private final byte[] message;

    @Override
    public int attack(ArrayList<Boolean> cipher, Stopwatch stopwatch) {

        for (int i = 0; i < cipher.size(); i++) {
            cipher.set(i, !cipher.get(i));
            if (!decrypt(cipher)) {
                ArrayList<Boolean> z = new ArrayList<>();
                for(int j = 0; j < cipher.size(); j++) {
                    ArrayList<Boolean> cipherGuess = (ArrayList<Boolean>) cipher.clone();
                    cipherGuess.set(j, !cipherGuess.get(j));
                    z.add(decrypt(cipherGuess));
                }
                CipherAttack bruteForce = BruteForceAttack.builder()
                        .k(k)
                        .n(n)
                        .t(0)
                        .G(G).build();

                int attackResult = bruteForce.attack(MatrixOperations.addVectors(z, cipher), stopwatch);
                if(attackResult == 0) {
                    return 0;
                }

               break;
            }
        }

        return 0;
    }

    private boolean decrypt(ArrayList<Boolean> cipher) {
        try {
            return MatrixOperations.trimString(MatrixOperations.computeMessageRepresentative(decrypt.messageDecrypt(MatrixOperations.bitsToByteArray(MatrixOperations.convertToString(MatrixOperations.toLittleEndian(cipher)))), this.k).toString()).equals(MatrixOperations.trimString(MatrixOperations.computeMessageRepresentative(message, this.k).toString()));
        } catch (Exception e) {
            return false;
        }
    }

    private ArrayList<Boolean> flipFirstBits(ArrayList<Boolean> a, int n) {
        for (int i = 0; i < n; i++) {
            a.set(i, !a.get(i));
        }
        return a;
    }
}
