package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.StringLiteral;

public class StringLiteral3 extends Idc3 {
    private String value;

    public StringLiteral3(StringLiteral stringLiteral) {
        this.value = stringLiteral.getValue();
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
