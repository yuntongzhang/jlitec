package com.yuntongzhang.jlitec.ir3;

import java.util.Objects;

import com.yuntongzhang.jlitec.ast.IntegerLiteral;

public class IntegerLiteral3 extends Idc3 {
    private int value;

    public IntegerLiteral3(IntegerLiteral integerLiteral) {
        this.value = integerLiteral.getValue();
    }

    public IntegerLiteral3(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerLiteral3 that = (IntegerLiteral3) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
