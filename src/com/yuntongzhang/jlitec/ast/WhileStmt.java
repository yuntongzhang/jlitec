package com.yuntongzhang.jlitec.ast;

import java.util.List;

public class WhileStmt extends Stmt {
    private Expression condition;
    private List<Stmt> body;

    public WhileStmt(Expression condition, List<Stmt> body, Node.Location loc) {
        super(loc);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void prettyPrint(int indentation) {
        String one = ("While(" + condition.toString() + ")").indent(indentation);
        String two = "{".indent(indentation);
        String three = "}".indent(indentation);

        System.out.print(one);
        System.out.print(two);
        for (Stmt stmt : body) {
            stmt.prettyPrint(indentation + 2);
        }
        System.out.print(three);
    }
}
