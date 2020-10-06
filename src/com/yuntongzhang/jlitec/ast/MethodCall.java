package com.yuntongzhang.jlitec.ast;

import java.util.List;
import java.util.stream.Collectors;

public class MethodCall extends Atom {
    private Atom atom;
    private List<Expression> expressionList;

    public MethodCall(Atom atom, List<Expression> expressionList) {
        this.atom = atom;
        this.expressionList = expressionList;
    }

    @Override
    public String toString() {
        String expListString = expressionList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        return "[" + atom.toString() + "(" + expListString + ")]";
    }
}
