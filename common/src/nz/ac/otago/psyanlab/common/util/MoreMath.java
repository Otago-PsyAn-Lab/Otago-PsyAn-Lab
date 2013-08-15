package nz.ac.otago.psyanlab.common.util;

/**
 * Useful math functions that aren't in java.lang.Math
 */
public class MoreMath {
    /**
     * If the input value lies outside of the specified range, return the nearer
     * bound. Otherwise, return the input value, unchanged.
     */
    public static int clamp(int input, int lowerBound, int upperBound) {
        if (input < lowerBound) return lowerBound;
        if (input > upperBound) return upperBound;
        return input;
    }

    /**
     * If the input value lies outside of the specified range, return the nearer
     * bound. Otherwise, return the input value, unchanged.
     */
    public static float clamp(float input, float lowerBound, float upperBound) {
        if (input < lowerBound) return lowerBound;
        if (input > upperBound) return upperBound;
        return input;
    }

    /**
     * If the input value lies outside of the specified range, return the nearer
     * bound. Otherwise, return the input value, unchanged.
     */
    public static double clamp(double input, double lowerBound, double upperBound) {
        if (input < lowerBound) return lowerBound;
        if (input > upperBound) return upperBound;
        return input;
    }
}