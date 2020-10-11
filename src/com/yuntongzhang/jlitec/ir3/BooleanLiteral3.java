package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.BooleanLiteral;

public class BooleanLiteral3 extends Idc3 {
    private boolean value;

    public BooleanLiteral3(BooleanLiteral booleanLiteral) {
        this.value = booleanLiteral.getValue();
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
