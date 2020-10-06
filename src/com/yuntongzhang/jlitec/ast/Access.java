package com.yuntongzhang.jlitec.ast;

public class Access extends Atom {
    private Atom atom;
    private Identifier id;

    public Access(Atom atom, Identifier id) {
        this.atom = atom;
        this.id = id;
    }

    @Override
    public String toString() {
        return atom.toString() + "." + id.toString();
    }
}
