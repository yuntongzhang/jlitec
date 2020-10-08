package com.yuntongzhang.jlitec.ast;

public class Access extends Atom {
    private Atom atom;
    private Identifier id;

    public Access(Atom atom, Identifier id, Node.Location loc) {
        super(loc);
        this.atom = atom;
        this.id = id;
    }

    public Atom getAtom() {
        return atom;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public String toString() {
        return atom.toString() + "." + id.toString();
    }
}
