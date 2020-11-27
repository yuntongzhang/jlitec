package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Println3 extends Stmt3 {
    private Idc3 idc3;

    public Println3(Idc3 idc3) {
        this.idc3 = idc3;
    }

    public Idc3 getArg() {
        return this.idc3;
    }

    @Override
    public Set<Id3> getUseSet() {
        HashSet<Id3> result = new HashSet<>();
        if (idc3 instanceof Id3) {
            result.add((Id3) idc3);
        }
        return result;
    }

    @Override
    public void replaceVars(Map<Id3, Id3> replacementScheme) {
        if (idc3 instanceof Id3 && replacementScheme.containsKey(idc3)) {
            this.idc3 = replacementScheme.get(idc3);
        }
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("println(" +  idc3.toString() + ");").indent(indentation);
        System.out.print(toPrint);
    }
}
