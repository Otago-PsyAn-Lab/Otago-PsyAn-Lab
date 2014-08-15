
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

public enum TokenType {
    AND,
    ASTERISK,
    BANG,
    CARET,
    COLON,
    COMMA,
    EOF,
    EQUALS,
    FLOAT,
    INTEGER,
    LEFT_BRACKET,
    LEFT_PAREN,
    LESS_THAN,
    LESS_THAN_OR_EQUAL_TO,
    MINUS,
    MORE_THAN,
    MORE_THAN_OR_EQUAL_TO,
    NAME,
    OR,
    PERCENT,
    PLUS,
    QUESTION,
    RIGHT_BRACKET,
    RIGHT_PAREN,
    SLASH,
    STRING,
    TILDE,
    XOR,
    NOT_EQUALS;

    public static final int LONGEST_PUNCTUATOR_LENGTH = 3;

    public static int longestToken() {
        return LONGEST_PUNCTUATOR_LENGTH;
    }

    /**
     * If the TokenType represents a punctuator (i.e. a token that can split an
     * identifier like '+', this will get its text.
     */
    public String punctuator() {
        switch (this) {
            case OR:
                return "or";
            case AND:
                return "and";
            case XOR:
                return "xor";
            case LEFT_PAREN:
                return "(";
            case RIGHT_PAREN:
                return ")";
            case LEFT_BRACKET:
                return "[";
            case RIGHT_BRACKET:
                return "]";
            case COMMA:
                return ",";
            case NOT_EQUALS:
                return "<>";
            case EQUALS:
                return "=";
            case LESS_THAN:
                return "<";
            case MORE_THAN:
                return ">";
            case LESS_THAN_OR_EQUAL_TO:
                return "<=";
            case MORE_THAN_OR_EQUAL_TO:
                return ">=";
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case ASTERISK:
                return "*";
            case SLASH:
                return "/";
            case PERCENT:
                return "%";
            case CARET:
                return "^";
            case TILDE:
                return "~";
            case BANG:
                return "!";
            case QUESTION:
                return "?";
            case COLON:
                return ":";
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return punctuator();
    }
}
