package vu.mif.ingvaras.galinskas;

import java.util.ArrayList;

public class LinearAlgebraSolver {

    public static ArrayList<Boolean> solveEquation(BinaryMatrix A) {

        int nOfIterations = Math.min(A.getNOfRows(), A.getNOfColumns() - 1);
        for(int col = 0; col < nOfIterations; col++) {
            if(!A.getValue(col, col)) {
                A.swapRows(col, A.findFirstNonZeroColumnValue(col, col));
            }
            for(int row = col + 1; row < A.getNOfRows(); row++) {
                if(A.getValue(row, col))
                    A.subtract(row, col);
            }
        }
        for(int col = nOfIterations - 1; col >= 0; col--) {
            for(int row = col - 1; row >= 0; row--) {
                if(A.getValue(row, col))
                    A.subtract(row, col);
            }
        }

        return A.getLastColumn();
    }



}
