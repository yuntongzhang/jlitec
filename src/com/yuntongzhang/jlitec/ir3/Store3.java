package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Store3 extends Stmt3 {
    private Id3 var;

    public Store3(Id3 var) {
        this.var = var;
    }

    public Id3 getVar() {
        return var;
    }

    @Override
    public Set<Id3> getUseSet() {
        Set<Id3> result = new HashSet<>();
        result.add(var);
        return result;
    }

    @Override
    public void replaceVars(Map<Id3, Id3> replacementScheme) {
        if (replacementScheme.containsKey(var)) this.var = replacementScheme.get(var);
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("store " + var.toString() + ";").indent(indentation);
        System.out.print(toPrint);
    }
}
