package com.yuntongzhang.jlitec.ast;

import java.util.Optional;

public class ReturnStmt extends Stmt {
    private Optional<Expression> expression;

    public ReturnStmt(Node.Location loc) {
        super(loc);
        this.expression = Optional.empty();
    }

    public ReturnStmt(Expression exp, Node.Location loc) {
        super(loc);
        this.expression = Optional.of(exp);
    }

    public boolean hasReturnValue() {
        return expression.isPresent();
    }

    // should always check hasReturnValue before calling this
    public Expression getReturnValue() {
        if (expression.isEmpty()) {
            return null;
        } else {
            return expression.get();
        }
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = "";
        if (expression.isEmpty()) {
            toPrint = "Return;".indent(indentation);
        } else {
            toPrint = ("Return " + expression.get().toString() + ";").indent(indentation);
        }
        System.out.print(toPrint);
    }
}
