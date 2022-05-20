package vu.mif.ingvaras.galinskas.attacks;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;

public interface CipherAttack {

    int attack(ArrayList<Boolean> cipher, Stopwatch stopwatch);
}
