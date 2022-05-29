package vu.mif.ingvaras.galinskas.attacks;

import com.google.common.base.Stopwatch;
import lombok.Builder;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import vu.mif.ingvaras.galinskas.math.LinearAlgebraSolver;
import vu.mif.ingvaras.galinskas.math.MatrixOperations;
import vu.mif.ingvaras.galinskas.math.domain.BinaryMatrix;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Builder
public class MessageResendAttack implements CipherAttack {

    private final int k;
    private int n;
    private final ArrayList<Boolean> message;
    private final GF2Matrix G;
    private final ArrayList<Boolean> additionalCipher;

    @Override
    public int attack(ArrayList<Boolean> cipher, Stopwatch stopwatch) {

        ArrayList<Boolean> L = MatrixOperations.addVectors(cipher, additionalCipher);
        BinaryMatrix G = new BinaryMatrix(n, k, MatrixOperations.trimString(this.G.computeTranspose().toString()));
        for(int i = 0; i < n; i++) {
            if(L.get(i)) {
                G.remove(i);
                cipher.remove(i);
                L.remove(i);
                i--;
                n--;
            }
        }

        while(true) {
            BinaryMatrix gClone = new BinaryMatrix(n, k, G.getRepresentation());
            ArrayList<Boolean> cipherClone = new ArrayList<>(cipher);
            for(int i = 0; i < n-k; i++) {
                int randomIndex = getRandomNumber(0, gClone.getNOfRows());
                gClone.remove(randomIndex);
                cipherClone.remove(randomIndex);
            }
            gClone.appendOneColumn(cipherClone);
            ArrayList<Boolean> mGuess = LinearAlgebraSolver.solveEquation(gClone);
            if (mGuess != null && mGuess.equals(message)) {
                System.out.println("Success");
                System.out.println("message found: " + MatrixOperations.convertToString(mGuess));
                stopwatch.stop();
                System.out.println("Time elapsed: "+ stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms\n");
                return 0;
            }
        }
        //ErrorVectorPermutationGenerator zGenerator = new ErrorVectorPermutationGenerator((int) hammingWeight/2, (int) hammingWeight);

        /*CipherAttack bruteForce = BruteForceAttack.builder()
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
        }*/
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
