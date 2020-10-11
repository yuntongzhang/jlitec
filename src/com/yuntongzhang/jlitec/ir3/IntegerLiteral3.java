package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.IntegerLiteral;

public class IntegerLiteral3 extends Idc3 {
    private int value;

    public IntegerLiteral3(IntegerLiteral integerLiteral) {
        this.value = integerLiteral.getValue();
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
