
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.model.Operand;

public class ExpressionParser {

    /**
     * Processes the expression. Determines the type (literal or expression),
     * generates a list of variable types matches for each variable detected in
     * the expression, formats the expression and maintains the relative caret
     * position.
     * <p>
     * Artifacts of the base operand are preserved as much as possible.
     * </p>
     * 
     * @param expression Expression to process.
     * @param baseOperand Base operand type for expression.
     * @param caretPosition Current position of caret in the text. The result
     *            will give a new position of the caret per the formatted
     *            expression.
     * @return Result of the processed expression.
     */
    public static Result process(String expression, Operand baseOperand, int caretPosition) {
        Result r = new Result();
        r.caretPosition = caretPosition;
        return r;
    }

    public static class Result {
        public int caretPosition;

        public boolean isValid;

        public Operand operand;
    }
}
