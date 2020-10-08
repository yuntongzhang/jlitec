package com.yuntongzhang.jlitec.exceptions;

import com.yuntongzhang.jlitec.ast.Node;

public class NonDistinctNameError extends SemanticError {
    public NonDistinctNameError(String msg, Node.Location loc) {
        super(msg, loc);
    }
}
