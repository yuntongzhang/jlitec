package com.yuntongzhang.jlitec.asm;

import com.yuntongzhang.jlitec.ast.Operator;

public enum Cond {
    EQ("eq"), // equal
    NE("ne"), // not equal
    MI("mi"), // minus or negative result
    PL("pl"), // positive or zero result
    VS("vs"), // overflow
    VC("vc"), // no overflow
    HS("hs"), // unsigned higher or same
    HI("hi"), // unsigned higher
    LS("ls"), // unsigned lower or same
    LO("lo"), // unsigned lower
    GE("ge"), // singed greater than or equal
    GT("gt"), // signed greate than
    LE("le"), // signed less than or equal
    LT("lt"), // signed less than
    AL(""); // always (this is the default)

    private final String name;

    Cond(String name) {
        this.name = name;
    }

    public static Cond fromOperator(Operator operator) {
        switch (operator) {
            case LT:
                return LT;
            case GT:
                return GT;
            case LEQ:
                return LE;
            case GEQ:
                return GE;
            case EQ:
                return EQ;
            case NEQ:
                return NE;
            default:
                return AL;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
