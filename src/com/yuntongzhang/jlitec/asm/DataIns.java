package com.yuntongzhang.jlitec.asm;

import com.yuntongzhang.jlitec.ast.Operator;

public class DataIns extends InsArm {
    private Opcode opcode;
    private Cond cond;
    private boolean updateFlag;
    private Reg rd;
    private Reg rn;
    private Operand2 operand2;

    public DataIns(Opcode opcode, Cond cond, boolean updateFlag, Reg rd, Reg rn, Operand2 operand2) {
        this.opcode = opcode;
        this.cond = cond;
        this.updateFlag = updateFlag;
        this.rd = rd;
        this.rn = rn;
        this.operand2 = operand2;
    }

    public DataIns(Opcode opcode, Reg rd, Reg rn, Operand2 operand2) {
        this(opcode, Cond.AL, false, rd, rn, operand2);
    }

    @Override
    public String toString() {
        String updateFlagString = updateFlag ? "S" : "";
        return opcode.name + updateFlagString + cond.toString() + " "
                + rd.toString() + "," + rn.toString() + "," + operand2.toString();
    }

    public enum Opcode {
        ADD("add"),
        SUB("sub"),
        RSB("rsb"),
        AND("and"),
        ORR("orr");

        private String name;
        Opcode(String name) {
            this.name = name;
        }

        public static Opcode fromOperator(Operator operator) {
            switch (operator) {
                case AND:
                    return AND;
                case OR:
                    return ORR;
                case PLUS:
                    return ADD;
                case MINUS:
                    return SUB;
                default: // should never happen
                    return RSB;
            }
        }
    }
}
