package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Return3 extends Stmt3 {
    private Optional<Id3> returnVal;

    public Return3() {
        this.returnVal = Optional.empty();
    }

    public Return3(Id3 returnVal) {
        this.returnVal = Optional.of(returnVal);
    }

    // return null if no return value
    public Id3 getReturnVal() {
        return returnVal.orElse(null);
    }

    @Override
    public Set<Id3> getUseSet() {
        HashSet<Id3> result = new HashSet<>();
        returnVal.ifPresent(result::add);
        return result;
    }

    @Override
    public void replaceVars(Map<Id3, Id3> replacementScheme) {
        if (returnVal.isPresent()) {
            Id3 oldVal = returnVal.get();
            if (replacementScheme.containsKey(oldVal)) {
                this.returnVal = Optional.of(replacementScheme.get(oldVal));
            }
        }
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = "";
        if (returnVal.isEmpty()) {
            toPrint = "return;".indent(indentation);
        } else {
            toPrint = ("return " + returnVal.get().toString() + ";").indent(indentation);
        }
        System.out.print(toPrint);
    }
}
