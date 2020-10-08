package com.yuntongzhang.jlitec.ast;

public class New extends Atom {
    private String className;

    public New(String cname, Node.Location loc) {
        super(loc);
        this.className = cname;
    }

    @Override
    public String toString() {
        return "[new " + className + "()]";
    }
}
