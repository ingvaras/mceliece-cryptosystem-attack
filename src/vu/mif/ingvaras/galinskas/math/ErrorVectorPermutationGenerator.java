package vu.mif.ingvaras.galinskas.math;

import java.util.ArrayList;
import java.util.Collections;

public class ErrorVectorPermutationGenerator {

    private final CombinationGenerator combinationGenerator;
    private final ArrayList<Boolean> emptyBinaryVector;
    private final int weight;
    private final int length;

    public ErrorVectorPermutationGenerator(int weight, int length) {
        emptyBinaryVector = new ArrayList<>();
        for(int i = 0; i < length; i++) {
            emptyBinaryVector.add(false);
        }
        this.weight = weight;
        this.length = length;
        combinationGenerator = new CombinationGenerator(length, weight);
    }

    public ArrayList<Boolean> nextPermutation() {
        ArrayList<Boolean> binaryVector = new ArrayList<>(emptyBinaryVector);
        if(!combinationGenerator.hasMore() || weight == 0)
            return new ArrayList<>(Collections.nCopies(length, Boolean.FALSE));
        int[] combination = combinationGenerator.getNext();
        for(int bit : combination) {
            binaryVector.set(bit, true);
        }
        return binaryVector;
    }

    public boolean hasMore() {
        return weight != 0 && combinationGenerator.hasMore();
    }

}
