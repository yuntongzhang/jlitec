package com.yuntongzhang.jlitec.ast;

public class New extends Atom {
    private String className;

    public New(String cname) {
        this.className = cname;
    }

    @Override
    public String toString() {
        return "[new " + className + "()]";
    }
}
