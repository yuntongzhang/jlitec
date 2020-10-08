package com.yuntongzhang.jlitec.exceptions;

import com.yuntongzhang.jlitec.ast.Node;

public class SemanticError extends Exception {
    public SemanticError(String msg) {
        super(msg);
    }

    public SemanticError(String msg, Node.Location loc) {
        super("<Line " + loc.getLine() + ", Column " + loc.getColumn() + "> : " + msg);
    }
}
