
package nz.ac.otago.psyanlab.common.expression.expressions;

public interface ExpressionVisitor {
    void visit(ConditionalExpression expression);

    void visit(FloatExpression expression);

    void visit(IntegerExpression expression);

    void visit(NameExpression expression);

    void visit(InfixExpression expression);

    void visit(PostfixExpression expression);

    void visit(PrefixExpression expression);

    void visit(StringExpression expression);
}
