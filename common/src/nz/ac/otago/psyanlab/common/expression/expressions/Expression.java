
package nz.ac.otago.psyanlab.common.expression.expressions;

/**
 * Interface for all expression AST node classes.
 */
public interface Expression {
    void accept(ExpressionVisitor visitor);

    int getPrecedence();
}
