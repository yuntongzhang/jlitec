package com.yuntongzhang.jlitec.exceptions;

import com.yuntongzhang.jlitec.ast.Node;

public class TypeError extends SemanticError {
    public TypeError(String msg, Node.Location loc) {
        super(msg, loc);
    }
}
