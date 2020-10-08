package com.yuntongzhang.jlitec.ast;

import java.util.List;
import java.util.stream.Collectors;

public class MethodCall extends Atom {
    private Atom atom;
    private List<Expression> expressionList;

    public MethodCall(Atom atom, List<Expression> expressionList, Node.Location loc) {
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
    public String toString() {
        String expListString = expressionList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        return "[" + atom.toString() + "(" + expListString + ")]";
    }
}
