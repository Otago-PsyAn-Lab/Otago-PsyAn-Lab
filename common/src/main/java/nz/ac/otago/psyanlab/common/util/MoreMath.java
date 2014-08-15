/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

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