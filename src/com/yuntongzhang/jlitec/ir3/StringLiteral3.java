package com.yuntongzhang.jlitec.ir3;

import java.util.Objects;

import com.yuntongzhang.jlitec.ast.StringLiteral;

public class StringLiteral3 extends Idc3 {
    private String value;

    public StringLiteral3(StringLiteral stringLiteral) {
        this.value = stringLiteral.getValue();
    }

    public StringLiteral3(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }

    public int getLength() {
        return value.length();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringLiteral3 that = (StringLiteral3) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
