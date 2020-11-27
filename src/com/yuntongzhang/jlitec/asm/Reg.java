package com.yuntongzhang.jlitec.asm;

import java.util.ArrayList;
import java.util.List;

public enum Reg {
    R0("r0"),
    R1("r1"),
    R2("r2"),
    R3("r3"),
    R4("r4"),
    R5("r5"),
    R6("r6"),
    R7("r7"),
    R8("r8"),
    R9("r9"),
    R10("r10"),
    FP("fp"),
    R12("r12"),
    SP("sp"),
    LR("lr"),
    PC("pc");

    private final String name;

    Reg(String name) {
        this.name = name;
    }

    // for convenience when preparing arguments
    public static Reg fromInt(int i) {
        switch (i) {
            case 0:
                return R0;
            case 1:
                return R1;
            case 2:
                return R2;
            case 3:
                return R3;
            default: // should not happen
                return R12;
        }
    }

    // use List to enforce some order because colors in graph coloring should have order
    public static List<Reg> allAllocatableRegs() {
        List<Reg> result = new ArrayList<>();
        result.add(R4);
        result.add(R5);
        result.add(R6);
        result.add(R7);
        result.add(R8);
        result.add(R9);
        result.add(R10);
        result.add(R12);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
