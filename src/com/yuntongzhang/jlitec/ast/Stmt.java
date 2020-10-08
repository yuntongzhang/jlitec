package com.yuntongzhang.jlitec.ast;

public abstract class Stmt extends Node implements PrettyPrintable {
    public Stmt(Location loc) {
        super(loc);
    }
}
