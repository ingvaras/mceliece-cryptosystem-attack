package vu.mif.ingvaras.galinskas.math.domain;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class BinaryMatrix {

    private int nOfRows;
    private int nOfColumns;
    private String representation;
    private final ArrayList<ArrayList<Boolean>> matrix = new ArrayList<>();

    public BinaryMatrix(int nOfRows, int nOfColumns, String representation) {
        this.nOfRows = nOfRows;
        this.nOfColumns = nOfColumns;
        this.representation = representation;
        for(int i = 0; i < this.nOfRows; i++) {
            matrix.add(createBinaryVector(
                    representation.substring(i * this.nOfColumns, i * this.nOfColumns + this.nOfColumns)));
        }
    }

    public BinaryMatrix swapRows(int rowIndex1, int rowIndex2) {
        ArrayList<Boolean> tmp = matrix.get(rowIndex1);
        matrix.set(rowIndex1, matrix.get(rowIndex2));
        matrix.set(rowIndex2, tmp);
        return this;
    }

    public BinaryMatrix subtract(int rowIndex1, int rowIndex2) {
        ArrayList<Boolean> previousRow = matrix.get(rowIndex1);
        ArrayList<Boolean> rowToSubtract = matrix.get(rowIndex2);
        ArrayList<Boolean> newRow = new ArrayList<>();
        for(int i = 0; i < nOfColumns; i++) {
            newRow.add(Boolean.logicalXor(previousRow.get(i), rowToSubtract.get(i)));
        }
        matrix.set(rowIndex1, newRow);
        return this;
    }

    public BinaryMatrix appendOneColumn(ArrayList<Boolean> column) {
        for(int row = 0; row < nOfRows; row++) {
            ArrayList<Boolean> currentRow = matrix.get(row);
            currentRow.add(column.get(row));
            matrix.set(row, currentRow);
        }
        this.nOfColumns++;
        return this;
    }

    public BinaryMatrix remove(int row) {
        if(row == 0)
            representation = representation.substring(this.nOfColumns);
        else if(row == nOfRows-1)
            representation = representation.substring(0, representation.length()-nOfColumns);
        else
            representation = representation.substring(0, nOfColumns * row) + representation.substring(nOfColumns + nOfColumns * row);
        matrix.remove(row);
        nOfRows--;
        return this;
    }

    public ArrayList<Boolean> leftMultiply(ArrayList<Boolean> a) {
        ArrayList<Boolean> result = new ArrayList<>();
        for(int col = 0; col < nOfColumns; col++) {
            boolean value = Boolean.FALSE;
            for (int row = 0; row < nOfRows; row++) {
                value = Boolean.logicalXor(value, Boolean.logicalAnd(this.getValue(row, col), a.get(row)));
            }
            result.add(value);
        }
        return result;
    }

    public Boolean getValue(int row, int col) {
        return matrix.get(row).get(col);
    }

    public ArrayList<Boolean> getLastColumn() {
        ArrayList<Boolean> lastColumn = new ArrayList<>();
        for(int col = 0; col < getNOfColumns() - 1; col++) {
            lastColumn.add(getValue(col, getNOfColumns() - 1));
        }
        return lastColumn;
    }

    public int getNOfRows() {
        return nOfRows;
    }

    public int getNOfColumns() {
        return nOfColumns;
    }

    public String getRepresentation() { return representation; }

    public int findFirstNonZeroColumnValue(int startingRow, int col) {
        for(int i = startingRow; i < nOfRows; i++) {
            if(matrix.get(i).get(col))
                return i;
        }
        return -1;
    }

    public static ArrayList<Boolean> createBinaryVector(String representation) {
        ArrayList<Boolean> binaryVector = new ArrayList<>();
        for(char bit : representation.toCharArray()) {
            binaryVector.add(bit == '1');
        }
        return binaryVector;
    }
}
