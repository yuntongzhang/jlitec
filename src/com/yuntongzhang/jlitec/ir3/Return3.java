package com.yuntongzhang.jlitec.ir3;

import java.util.Optional;

public class Return3 extends Stmt3 {
    private Optional<Id3> returnVal;

    public Return3() {
        this.returnVal = Optional.empty();
    }

    public Return3(Id3 returnVal) {
        this.returnVal = Optional.of(returnVal);
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
