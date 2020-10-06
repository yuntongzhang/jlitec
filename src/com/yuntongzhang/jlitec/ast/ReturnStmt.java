package com.yuntongzhang.jlitec.ast;

import java.util.Optional;

public class ReturnStmt implements Stmt {
    private Optional<Expression> expression;

    public ReturnStmt() {
        this.expression = Optional.empty();
    }

    public ReturnStmt(Expression exp) {
        this.expression = Optional.of(exp);
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
