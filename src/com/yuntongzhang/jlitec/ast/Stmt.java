package com.yuntongzhang.jlitec.ast;

import com.yuntongzhang.jlitec.ir3.Label3;

public abstract class Stmt extends Node implements PrettyPrintable {
    // facilitate ir3 translation
    public Label3 next = null;

    public Stmt(Location loc) {
        super(loc);
    }
}
