
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
    XOR;

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
