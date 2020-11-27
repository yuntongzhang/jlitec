package com.yuntongzhang.jlitec.asm;

public class CmpIns extends InsArm {
    private Opcode opcode;
    private Cond cond;
    private Reg rn;
    private Operand2 operand2;

    public CmpIns(Opcode opcode, Cond cond, Reg rn, Operand2 operand2) {
        this.opcode = opcode;
        this.cond = cond;
        this.rn = rn;
        this.operand2 = operand2;
    }

    public CmpIns(Reg rn, Operand2 operand2) {
        this(Opcode.CMP, Cond.AL, rn, operand2);
    }

    @Override
    public String toString() {
        return opcode.name + cond.toString() + " " + rn.toString() + "," + operand2.toString();
    }

    public static enum Opcode {
        CMP("cmp"),
        CMN("cmn"),
        TST("tst"),
        TEQ("teq");

        private String name;

        Opcode(String name) {
            this.name = name;
        }
    }
}
