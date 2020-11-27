package com.yuntongzhang.jlitec.asm;

import java.util.List;

public class StackIns extends InsArm {
    private Opcode opcode;
    private List<Reg> regList;

    public StackIns(Opcode opcode, List<Reg> regList) {
        this.opcode = opcode;
        this.regList = regList;
    }

    @Override
    public String toString() {
        String result = opcode.name + " {";
        for (int i = 0; i < regList.size(); i++) {
            result += regList.get(i).toString();
            if (i < regList.size() - 1) {
                result += ",";
            }
        }
        result += "}";
        return result;
    }

    public enum Opcode {
        PUSH("push"),
        POP("pop");
        private String name;
        Opcode(String name) {
            this.name = name;
        }
    }
}
