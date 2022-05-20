package vu.mif.ingvaras.galinskas.attacks;

import com.google.common.base.Stopwatch;
import lombok.Builder;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import vu.mif.ingvaras.galinskas.math.MatrixOperations;
import vu.mif.ingvaras.galinskas.math.domain.BinaryMatrix;

import java.util.ArrayList;

@Builder
public class PartiallyKnownTextAttack implements CipherAttack {

    private final int k;
    private final int n;
    private final int t;
    private final GF2Matrix G;
    private final int kr;
    private final ArrayList<Boolean> mr;

    @Override
    public int attack(ArrayList<Boolean> cipher, Stopwatch stopwatch) {

        String representativeTransposeG = MatrixOperations.trimString(this.G.computeTranspose().toString());
        StringBuilder representativeTransposeGl = new StringBuilder();
        for(int i = 0; i < n; i++) {
            representativeTransposeGl.append(representativeTransposeG, i * k, ((i + 1) * k) - kr);
        }

        String representativeGr = MatrixOperations.trimString(this.G.toString()).substring(n * (k - kr), n * k);
        BinaryMatrix Gr = new BinaryMatrix(kr, n, representativeGr);
        ArrayList<Boolean> cipherR = Gr.leftMultiply(mr);

        CipherAttack bruteForce = BruteForceAttack.builder()
                .k(k - kr)
                .n(n)
                .t(t)
                .representativeG(representativeTransposeGl.toString()).build();

        int attackResult = bruteForce.attack(MatrixOperations.addVectors(cipherR, cipher), stopwatch);
        if (attackResult == 0) {
            return 0;
        }

        return -1;
    }
}
