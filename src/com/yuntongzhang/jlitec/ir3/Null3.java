package com.yuntongzhang.jlitec.ir3;

public class Null3 extends Idc3 {
    public Null3() {
    }

    // null takes object reference as equals and hashcode
    // since different instances of null should not be considered in CSE

    @Override
    public String toString() {
        return "NULL";
    }
}
