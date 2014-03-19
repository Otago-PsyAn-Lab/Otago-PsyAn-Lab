
package nz.ac.otago.psyanlab.common.expression.expressions;

/**
 * A prefix unary arithmetic expression like "!a" or "-b".
 */
public class LinkExpression implements Expression {
    private final Expression mChild;

    public LinkExpression(Expression child) {
        mChild = child;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int getPrecedence() {
        return mChild.getPrecedence();
    }

    public Expression getChild() {
        return mChild;
    }
}
