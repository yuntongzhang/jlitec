package com.yuntongzhang.jlitec.exceptions;

import com.yuntongzhang.jlitec.ast.Node;

public class NameNotFoundError extends SemanticError {
    // for the case where location info is not available
    public NameNotFoundError(String msg) {
        super(msg);
    }

    public NameNotFoundError(String msg, Node.Location loc) {
        super(msg, loc);
    }
}
