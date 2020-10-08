package com.yuntongzhang.jlitec.exceptions;

import com.yuntongzhang.jlitec.ast.Node;

public class NonDistinctNameError extends Exception {
    public  NonDistinctNameError(String msg, Node.Location loc) {
        super("<Line " + loc.getLine() + ", Column " + loc.getColumn() + "> : " + msg);
    }
}
