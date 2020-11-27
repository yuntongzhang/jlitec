package com.yuntongzhang.jlitec.asm;

public class MovIns extends InsArm {
    private Opcode opcode;
    private Cond cond;
    private boolean updateFlag;
    private Reg rd;
    private Operand2 operand2;

    public MovIns(Opcode opcode, Cond cond, boolean updateFlag, Reg rd, Operand2 operand2) {
        this.opcode = opcode;
        this.cond = cond;
        this.updateFlag = updateFlag;
        this.rd = rd;
        this.operand2 = operand2;
    }

    public MovIns(Reg rd, Operand2 operand2) {
        this(Opcode.MOV, Cond.AL, false, rd, operand2);
    }

    public MovIns(Cond cond, Reg rd, Operand2 operand2) {
        this(Opcode.MOV, cond, false, rd, operand2);
    }

    public MovIns(Opcode opcode, Reg rd, Operand2 operand2) {
        this(opcode, Cond.AL, false, rd, operand2);
    }

    @Override
    public String toString() {
        String updateFlagString = updateFlag ? "S" : "";
        return opcode.name + updateFlagString + cond.toString() + " "
                + rd.toString() + "," + operand2.toString();
    }

    public enum Opcode {
        MOV("mov"),
        MVN("mvn");
        private String name;
        Opcode(String name) {
            this.name = name;
        }
    }
}
