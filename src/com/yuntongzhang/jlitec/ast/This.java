package com.yuntongzhang.jlitec.ast;

public class This extends Atom {
    public This(Node.Location loc) {
        super(loc);
    }

    @Override
    public String toString() {
        return "this";
    }
}
