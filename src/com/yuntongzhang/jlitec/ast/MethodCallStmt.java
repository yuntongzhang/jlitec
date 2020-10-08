package com.yuntongzhang.jlitec.ast;

import java.util.List;
import java.util.stream.Collectors;

public class MethodCallStmt extends Stmt {
    private Atom atom;
    private List<Expression> expressionList;

    public MethodCallStmt(Atom atom, List<Expression> expressionList, Node.Location loc) {
        super(loc);
        this.atom = atom;
        this.expressionList = expressionList;
    }

    public Atom getAtom() {
        return atom;
    }

    public List<Expression> getExpressionList() {
        return expressionList;
    }

    @Override
    public void prettyPrint(int indentation) {
        String expListString = expressionList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        String toPrint = (atom.toString() + "(" + expListString + ");").indent(indentation);
        System.out.print(toPrint);
    }
}
