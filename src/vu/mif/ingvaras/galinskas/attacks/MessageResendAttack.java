package vu.mif.ingvaras.galinskas.attacks;

import com.google.common.base.Stopwatch;
import lombok.Builder;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import vu.mif.ingvaras.galinskas.math.ErrorVectorPermutationGenerator;
import vu.mif.ingvaras.galinskas.math.MatrixOperations;

import java.util.ArrayList;

@Builder
public class MessageResendAttack implements CipherAttack {

    private final int k;
    private final int n;
    private final int t;
    private final GF2Matrix G;
    private final ArrayList<Boolean> additionalCipher;

    @Override
    public int attack(ArrayList<Boolean> cipher, Stopwatch stopwatch) {

        ArrayList<Boolean> L = MatrixOperations.addVectors(cipher, additionalCipher);
        long hammingWeight = MatrixOperations.hammingWeight(L);

        ErrorVectorPermutationGenerator zGenerator = new ErrorVectorPermutationGenerator((int) hammingWeight/2, (int) hammingWeight);

        CipherAttack bruteForce = BruteForceAttack.builder()
                .k(k)
                .n(n)
                .t((int)(t - hammingWeight / 2))
                .G(G).build();

        while (zGenerator.hasMore()) {
            ArrayList<Boolean> zPartialGuess = MatrixOperations.computePartialCGuess(L, zGenerator.nextPermutation());
            ArrayList<Boolean> cPartialGuess = MatrixOperations.addVectors(zPartialGuess, cipher);

            int attackResult = bruteForce.attack(cPartialGuess, stopwatch);
            if(attackResult == 0) {
                return 0;
            }
        }
        return -1;
    }
}
