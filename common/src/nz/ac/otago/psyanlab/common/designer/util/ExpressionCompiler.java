
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler.Lexer.IdentityToken;
import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler.Lexer.NumberToken;
import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler.Lexer.OperatorToken;
import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler.Lexer.StringToken;
import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler.Lexer.UnknownToken;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

public class ExpressionCompiler {
    public static final int TOKEN_DECIMAL = 0x01;

    public static final int TOKEN_IDENTITY = 0x02;

    public static final int TOKEN_INTEGER = 0x04;

    public static final int TOKEN_LITERAL = 0x08;

    public static final int TOKEN_NUMBER = 0x10;

    public static final int TOKEN_OPERATOR = 0x20;

    public static final int TOKEN_STRING = 0x40;

    public static final int TOKEN_UNKNOWN = 0x100;

    private OperandCallbacks mCallbacks;

    private Lexer mLexer;

    public ExpressionCompiler(OperandCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void compile(String expression, HashMap<String, Long> operandIds) {
        ExpressionParser parser = new ExpressionParser(mCallbacks, operandIds);
        mLexer = new Lexer(parser);
        mLexer.lex(expression);
    }

    public String formatExpression() {
        return mLexer.formatExpression();
    }

    public TokenError getError() {
        return mLexer.getError();
    }

    public static class TokenError {
        public TokenError(int i, String error) {
            tokenIndex = i;
            errorString = error;
        }

        public int tokenIndex;

        public String errorString;
    }

    static class Lexer {
        protected static final int RESULT_CHAR_CONSUMED = 0x01;

        protected static final int RESULT_TERMINAL_REACHED = 0x02;

        private Token mCurrentToken;

        private ExpressionParser mParser;

        private ArrayList<Token> mTokens;

        public Lexer(ExpressionParser parser) {
            mParser = parser;
        }

        public String formatExpression() {
            StringBuilder sb = new StringBuilder();
            for (Token token : mTokens) {
                sb.append(token.toString());
            }
            return sb.toString();
        }

        public TokenError getError() {
            for (ListIterator<Token> iterator = mTokens.listIterator(); iterator.hasNext();) {
                Token token = iterator.next();
                if (token.hasError()) {
                    return new TokenError(iterator.nextIndex() - 1, token.getError());
                }
            }

            return null;
        }

        public ArrayList<Token> getTokens() {
            return mTokens;
        }

        public void lex(String expression) {
            mTokens = new ArrayList<Token>();
            final int length = expression.length();
            for (int offset = 0; offset < length;) {
                final int codepoint = expression.codePointAt(offset);
                processCodepoint(codepoint);
                offset += Character.charCount(codepoint);
            }
            mParser.completeTree();
            Log.d("DEBUG TREE", mParser.printState());
        }

        private void processCodepoint(final int codepoint) {
            if (mCurrentToken == null) {
                mCurrentToken = Token.newToken(codepoint);
            } else {
                int result = mCurrentToken.eat(codepoint);
                if ((result & RESULT_TERMINAL_REACHED) != 0) {
                    storeToken();
                    if ((result & RESULT_CHAR_CONSUMED) == 0) {
                        mCurrentToken = Token.newToken(codepoint);
                    }
                }
            }
        }

        private void storeToken() {
            Log.d("DEBUG TOKEN", mCurrentToken.toString());
            mParser.addToken(mCurrentToken);
            Log.d("DEBUG TREE", mParser.printState());
            mTokens.add(mCurrentToken);
        }

        static class IdentityToken extends Token {
            public static boolean validates(int codepoint) {
                return Character.isLetter(codepoint);
            }

            public IdentityToken(int codepoint) {
                super(codepoint);
            }

            @Override
            public int eat(int codepoint) {
                int r = 0;
                if (Character.isLetterOrDigit(codepoint)) {
                    mStringRepresentation.appendCodePoint(codepoint);
                    r |= RESULT_CHAR_CONSUMED;
                } else {
                    r |= RESULT_TERMINAL_REACHED;

                }
                return r;
            }

            @Override
            int getType() {
                return TOKEN_IDENTITY;
            }
        }

        static abstract class LiteralToken extends Token {
            public LiteralToken(int codepoint) {
                super(codepoint);
            }

            protected LiteralToken() {
            }

            @Override
            int getType() {
                return TOKEN_LITERAL;
            }
        }

        static class NumberToken extends LiteralToken {
            private static final String sValidCharacters = "0123456789.";

            public static boolean validates(int codepoint) {
                return Character.isDigit(codepoint) && isValidCharacter(codepoint);
            }

            private static boolean isValidCharacter(int codepoint) {
                return sValidCharacters.contains(new String(Character.toChars(codepoint)));
            }

            public NumberToken(int codepoint) {
                super(codepoint);
            }

            @Override
            public int eat(int codepoint) {
                int r = 0;

                if (isValidCharacter(codepoint)) {
                    mStringRepresentation.appendCodePoint(codepoint);
                    r |= RESULT_CHAR_CONSUMED;
                } else {
                    r |= RESULT_TERMINAL_REACHED;
                }

                return r;
            }

            @Override
            int getType() {
                return super.getType() | TOKEN_NUMBER;
            }
        }

        static class OperatorToken extends Token {
            private static final String[] sMultiCharOperators = new String[] {
                    "<=", ">=", "<>",
            };

            private static final String sValidCharacters = "^*/%+-!=<>()[,]";

            public static boolean validates(int codepoint) {
                return sValidCharacters.contains(new String(Character.toChars(codepoint)));
            }

            public OperatorToken(int codepoint) {
                super(codepoint);
            }

            @Override
            public int eat(int codepoint) {
                int r = RESULT_TERMINAL_REACHED;
                StringBuilder builder = new StringBuilder(mStringRepresentation);
                String op = builder.appendCodePoint(codepoint).toString();
                for (int i = 0; i < sMultiCharOperators.length; i++) {
                    if (TextUtils.equals(op, sMultiCharOperators[i])) {
                        mStringRepresentation = builder;
                        r |= RESULT_CHAR_CONSUMED;
                    }
                }
                return r;
            }

            @Override
            int getType() {
                return TOKEN_OPERATOR;
            }
        }

        static class StringToken extends LiteralToken {
            public static boolean validates(int codepoint) {
                return codepoint == (int)'\"';
            }

            private boolean mEscaping;

            public StringToken() {
                super();
            }

            @Override
            public int eat(int codepoint) {
                int r = 0;
                r |= RESULT_CHAR_CONSUMED;
                if (!mEscaping && codepoint == (int)'\"') {
                    r |= RESULT_TERMINAL_REACHED;
                } else if (!mEscaping && codepoint == (int)'\\') {
                    mEscaping = true;
                } else {
                    mEscaping = false;
                    mStringRepresentation.appendCodePoint(codepoint);
                }
                return r;
            }

            @Override
            int getType() {
                return super.getType() | TOKEN_STRING;
            }
        }

        static class UnknownToken extends Token {
            public UnknownToken(int codepoint) {
                super(codepoint);
            }

            @Override
            public int eat(int codepoint) {
                int r = 0;
                if (Character.isWhitespace(codepoint)) {
                    r |= RESULT_TERMINAL_REACHED;
                } else {
                    mStringRepresentation.appendCodePoint(codepoint);
                }
                return r;
            }

            @Override
            int getType() {
                return TOKEN_UNKNOWN;
            }
        }
    }

    static abstract class Token {
        static public Token newToken(int codepoint) {
            Token token = null;
            if (!Character.isWhitespace(codepoint)) {
                if (StringToken.validates(codepoint)) {
                    token = new StringToken();
                } else if (NumberToken.validates(codepoint)) {
                    token = new NumberToken(codepoint);
                } else if (IdentityToken.validates(codepoint)) {
                    token = new IdentityToken(codepoint);
                } else if (OperatorToken.validates(codepoint)) {
                    token = new OperatorToken(codepoint);
                } else {
                    token = new UnknownToken(codepoint);
                }
            }
            return token;
        }

        private boolean mError;

        private String mErrorString;

        protected StringBuilder mStringRepresentation;

        @Override
        public String toString() {
            return mStringRepresentation.toString();
        }

        public Token(int codepoint) {
            mStringRepresentation = new StringBuilder();
            mStringRepresentation.appendCodePoint(codepoint);
        }

        protected Token() {
        }

        public abstract int eat(int codepoint);

        public String getError() {
            return mErrorString;
        }

        public String getString() {
            return mStringRepresentation.toString();
        }

        public boolean hasError() {
            return mError;
        }

        public void markError(String error) {
            mError = true;
            mErrorString = error;
        }

        abstract int getType();
    }
}
