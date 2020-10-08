package com.yuntongzhang.jlitec.ast;

public class Null extends Atom {
    public Null(Node.Location loc) {
        super(loc);
    }

    @Override
    public String toString() {
        return "null";
    }
}
