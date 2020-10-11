package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.New;

public class New3 extends Exp3 {
    private String cname;

    public New3(New n) {
        this.cname = n.getClassName();
    }

    @Override
    public String toString() {
        return "new " + cname + "()";
    }
}
