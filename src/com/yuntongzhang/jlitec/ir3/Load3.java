package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Set;

public class Load3 extends Stmt3 {
    private Id3 var;

    public Load3(Id3 var) {
        this.var = var;
    }

    public Id3 getVar() {
        return var;
    }

    @Override
    public Set<Id3> getDefSet() {
        Set<Id3> result = new HashSet<>();
        result.add(var);
        return result;
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("load " + var.toString() + ";").indent(indentation);
        System.out.print(toPrint);
    }
}
