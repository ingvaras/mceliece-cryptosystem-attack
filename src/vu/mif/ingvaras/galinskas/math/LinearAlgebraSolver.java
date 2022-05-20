package vu.mif.ingvaras.galinskas.math;

import vu.mif.ingvaras.galinskas.math.domain.BinaryMatrix;

import java.util.ArrayList;

public class LinearAlgebraSolver {

    public static ArrayList<Boolean> solveEquation(BinaryMatrix A) {

        int nOfIterations = Math.min(A.getNOfRows(), A.getNOfColumns() - 1);
        for(int col = 0; col < nOfIterations; col++) {
            if(!A.getValue(col, col)) {
                int indexOfNonZeroColumn = A.findFirstNonZeroColumnValue(col, col);
                if(indexOfNonZeroColumn != -1)
                    A.swapRows(col, A.findFirstNonZeroColumnValue(col, col));
                else return null;
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
        for(int i = A.getNOfColumns() - 1; i < A.getNOfRows(); i++) {
            if(A.getValue(i, A.getNOfColumns() - 1))
                return null;
        }
        return A.getLastColumn();
    }
}
