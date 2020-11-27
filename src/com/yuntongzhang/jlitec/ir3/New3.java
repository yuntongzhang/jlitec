package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.New;

public class New3 extends Exp3 {
    private String cname;

    public New3(New n) {
        this.cname = n.getClassName();
    }

    public String getCname() {
        return cname;
    }

    // new takes object reference as equals and hashcode
    // since different instances of null should not be considered in CSE

    @Override
    public String toString() {
        return "new " + cname + "()";
    }
}
