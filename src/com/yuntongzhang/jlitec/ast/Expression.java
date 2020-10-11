package com.yuntongzhang.jlitec.ast;

import com.yuntongzhang.jlitec.ir3.Idc3;
import com.yuntongzhang.jlitec.ir3.Label3;

public abstract class Expression extends Node {
    // facilitate ir3 translation
    public Label3 trueLabel = null;
    public Label3 falseLabel = null;
    public Idc3 idc3 = null; // point to the corresponding generated idc3 Node

    // store result of type checking for future use
    public SType checkedType = null;

    public Expression(Node.Location loc) {
        super(loc);
    }
}
