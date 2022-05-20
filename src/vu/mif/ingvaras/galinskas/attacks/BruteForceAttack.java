package vu.mif.ingvaras.galinskas.attacks;

import com.google.common.base.Stopwatch;
import lombok.Builder;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import vu.mif.ingvaras.galinskas.math.ErrorVectorPermutationGenerator;
import vu.mif.ingvaras.galinskas.math.LinearAlgebraSolver;
import vu.mif.ingvaras.galinskas.math.MatrixOperations;
import vu.mif.ingvaras.galinskas.math.domain.BinaryMatrix;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Builder
public class BruteForceAttack implements CipherAttack {

    private final int k;
    private final int n;
    private final int t;
    private final GF2Matrix G;
    private final String representativeG;

    @Override
    public int attack(ArrayList<Boolean> cipher, Stopwatch stopwatch) {

        ErrorVectorPermutationGenerator generator = new ErrorVectorPermutationGenerator(t, n);

        BinaryMatrix G = new BinaryMatrix(n, k, representativeG == null ? MatrixOperations.trimString(this.G.computeTranspose().toString()) : representativeG);

        do {
            ArrayList<Boolean> cGuess = MatrixOperations.addVectors(generator.nextPermutation(), cipher);
            G.appendOneColumn(cGuess);
            ArrayList<Boolean> mGuess = LinearAlgebraSolver.solveEquation(G);
            if (mGuess != null) {
                System.out.println("Success");
                System.out.println("message found: " + MatrixOperations.convertToString(mGuess));
                stopwatch.stop();
                System.out.println("Time elapsed: "+ stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms\n");
                return 0;
            }
            G = new BinaryMatrix(n, k, representativeG == null ? MatrixOperations.trimString(this.G.computeTranspose().toString()) : representativeG);
        } while (generator.hasMore());
        return -1;
    }
}
