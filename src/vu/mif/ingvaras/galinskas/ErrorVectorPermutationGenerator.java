package vu.mif.ingvaras.galinskas;

import java.util.ArrayList;

public class ErrorVectorPermutationGenerator {

    private CombinationGenerator combinationGenerator;
    private ArrayList<Boolean> emptyBinaryVector;

    public ErrorVectorPermutationGenerator(int weight, int length) {
        emptyBinaryVector = new ArrayList<>();
        for(int i = 0; i < length; i++) {
            emptyBinaryVector.add(false);
        }
        combinationGenerator = new CombinationGenerator(length, weight);
    }

    public ArrayList<Boolean> nextPermutation() {
        ArrayList<Boolean> binaryVector = new ArrayList<>(emptyBinaryVector);
        if(!combinationGenerator.hasMore())
            return null;
        int[] combination = combinationGenerator.getNext();
        for(int bit : combination) {
            binaryVector.set(bit, true);
        }
        return binaryVector;
    }

    public boolean hasMore() {
        return combinationGenerator.hasMore();
    }

}
