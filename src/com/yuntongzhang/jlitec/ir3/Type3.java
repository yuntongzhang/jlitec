package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.SType;

public class Type3 {
    private String typeName;

    // construct from a corresponding SType
    public Type3(SType sType) {
        this.typeName = sType.getName();
    }

    // construct from the type name directly
    public Type3(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
