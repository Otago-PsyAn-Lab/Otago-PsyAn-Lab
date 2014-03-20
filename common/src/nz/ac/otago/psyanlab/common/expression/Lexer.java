
package nz.ac.otago.psyanlab.common.expression;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A very primitive lexer. Takes a string and splits it into a series of Tokens.
 * Operators and punctuation are mapped to unique keywords. Names, which can be
 * any series of letters, are turned into NAME tokens. All other characters are
 * ignored (except to separate names). Numbers and strings are not supported.
 * This is really just the bare minimum to give the parser something to work
 * with.
 */
public class Lexer implements Iterator<Token> {
    private static final String sValidNumberCharacters = "0123456789.";

    private static final String sValidPunctuationCharacters = "()^*/%+-!=<>[,]?:";

    private static boolean isEscapeCharacter(int cp) {
        return cp == '\\';
    }

    private static boolean isSpeechMark(final int codePoint) {
        return codePoint == '\"';
    }

    private static boolean isValidNumberCharacter(int codePoint) {
        return sValidNumberCharacters.contains(new String(Character.toChars(codePoint)));
    }

    private static boolean isValidPunctuationCharacter(int codePoint) {
        return sValidPunctuationCharacters.contains(new String(Character.toChars(codePoint)));
    }

    private int mOffset = 0;

    private final Map<String, TokenType> mPunctuators = new HashMap<String, TokenType>();

    private final String mText;

    StringBuilder mBuilder = new StringBuilder();

    /**
     * Creates a new Lexer to tokenize the given string.
     * 
     * @param text String to tokenize.
     */
    public Lexer(String text) {
        mOffset = 0;
        mText = text;

        // Register all of the TokenTypes that are explicit punctuators.
        for (TokenType type : TokenType.values()) {
            String punctuator = type.punctuator();
            if (punctuator != null) {
                mPunctuators.put(punctuator, type);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    /**
     * Gets the text that has been tokenised.
     * 
     * @return Tokenised component of text.
     */
    public String getTextLexed() {
        return mText.substring(0, mOffset);
    }

    /**
     * Gets the rest of the text that has not yet been tokenised.
     * 
     * @return Remaining untokenised text.
     */
    public String getTextRemainder() {
        return mText.substring(mOffset);
    }

    @Override
    public Token next() {
        Log.d("LEXER NEXT", "" + mOffset);
        while (mOffset < mText.length()) {
            final int codePoint = mText.codePointAt(mOffset);
            mOffset += Character.charCount(codePoint);
            mBuilder.setLength(0);

            Token token = null;
            if (isValidPunctuationCharacter(codePoint)) {
                token = tokenisePunctuation(codePoint);
            } else if (Character.isLetter(codePoint)) {
                // First code point is a letter so we must be a name
                // variable.
                token = tokeniseName(codePoint);
            } else if (isValidNumberCharacter(codePoint)) {
                // First code point is a number (or decimal separator) so we
                // must be a number of some kind.
                token = tokeniseNumber(codePoint);
            } else if (isSpeechMark(codePoint)) {
                token = tokeniseStringLiteral();
            } else {
                // Ignore all other characters (whitespace, etc.)
            }
            if (token != null) {
                return token;
            }
        }

        // Once we've reached the end of the string, just return EOF tokens.
        // We'll
        // just keeping returning them as many times as we're asked so that the
        // parser's lookahead doesn't have to worry about running out of tokens.
        return new Token(TokenType.EOF, "");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Reads until it reaches a character that isn't a valid name character.
     * Will also tokenise keywords appropriately.
     * 
     * @param codePoint Starting code point for the token.
     * @return Matched token.
     */
    private Token tokeniseName(int codePoint) {
        mBuilder.appendCodePoint(codePoint);
        while (mOffset < mText.length()) {
            int cp = mText.codePointAt(mOffset);
            if (!Character.isLetter(cp)) {
                break;
            }
            mBuilder.appendCodePoint(cp);
            mOffset += Character.charCount(cp);
        }
        String s = mBuilder.toString();
        String p = s.toLowerCase();
        mBuilder.setLength(0);
        if (mPunctuators.containsKey(p)) {
            return new Token(mPunctuators.get(p), p);
        }
        return new Token(TokenType.NAME, s);
    }

    /**
     * Reads until it reaches a character that isn't a valid number character.
     * Also determines whether the number is an integer or float.
     * 
     * @param codePoint Starting code point for the token.
     * @return Matched token.
     */
    private Token tokeniseNumber(int codePoint) {
        boolean hasDecimalPoint = false;
        mBuilder.appendCodePoint(codePoint);

        if (codePoint == '.') {
            hasDecimalPoint = true;
        }

        while (mOffset < mText.length()) {
            int cp = mText.codePointAt(mOffset);
            if (!isValidNumberCharacter(cp)) {
                break;
            }
            if (cp == '.') {
                if (hasDecimalPoint) {
                    break;
                }
                hasDecimalPoint = true;
            }
            mBuilder.appendCodePoint(cp);
            mOffset += Character.charCount(cp);
        }
        String s = mBuilder.toString();
        mBuilder.setLength(0);
        if (hasDecimalPoint) {
            return new Token(TokenType.FLOAT, s);
        }
        return new Token(TokenType.INTEGER, s);
    }

    /**
     * Reads as much to right as possible to match the longest punctuator
     * possible. If there is no match at the longest string, slowly backtrack to
     * get the longest match.
     * 
     * @param codePoint Current code point matching on.
     * @return Null if no match at this length, or token if match.
     */
    private Token tokenisePunctuation(final int codePoint) {
        mBuilder.appendCodePoint(codePoint);
        String punctuation = mBuilder.toString().toLowerCase();

        if (mText.length() != mOffset) {
            int cp = mText.codePointAt(mOffset);
            mOffset += Character.charCount(cp);
            if (isValidPunctuationCharacter(codePoint)) {
                Token token = tokenisePunctuation(cp);
                if (token != null) {
                    return token;
                }
            }
            // Backtrack because there wasn't a longer match.
            mOffset -= Character.charCount(cp);
        }
        if (mPunctuators.containsKey(punctuation)) {
            mBuilder.setLength(0);
            return new Token(mPunctuators.get(punctuation), punctuation);
        }
        return null;
    }

    /**
     * Reads until it reaches the terminal character for a string literal.
     * 
     * @return Matched token.
     */
    private Token tokeniseStringLiteral() {
        boolean escaping = false;

        while (mOffset < mText.length()) {
            int cp = mText.codePointAt(mOffset);
            mOffset += Character.charCount(cp);
            if (escaping) {
                escaping = false;
            } else if (isEscapeCharacter(cp)) {
                escaping = true;
                continue;
            } else if (isSpeechMark(cp)) {
                break;
            }
            mBuilder.appendCodePoint(cp);
        }
        String name = mBuilder.toString();
        mBuilder.setLength(0);
        return new Token(TokenType.STRING, name);
    }
}
