package com.yuntongzhang.jlitec.ir3;

public class Access3 extends Exp3 {
    private Id3 lhs;
    private Id3 rhs;

    public Access3(Id3 lhs, Id3 rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return lhs.toString() + "." + rhs.toString();
    }
}
