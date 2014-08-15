
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

package nz.ac.otago.psyanlab.common.expression;

/**
 * Defines the different precendence levels used by the infix parsers. These
 * determine how a series of infix expressions will be grouped. For example,
 * "a + b * c - d" will be parsed as "(a + (b * c)) - d" because "*" has higher
 * precedence than "+" and "-". Here, bigger numbers mean higher precedence.
 */
public class Precedence {
    // Ordered in increasing precedence.
    public static final int ASSIGNMENT = 1;

    public static final int CONDITIONAL = 2;

    public static final int OR = 3;

    public static final int XOR = 4;

    public static final int AND = 5;

    public static final int COMPARISON = 6;

    public static final int SUM = 7;

    public static final int SUBSTRING = 8;

    public static final int PRODUCT = 9;

    public static final int EXPONENT = 10;

    public static final int PREFIX = 11;

    public static final int POSTFIX = 12;

    public static final int CALL = 13;

    public static final int IDENTITY = 14;
}
