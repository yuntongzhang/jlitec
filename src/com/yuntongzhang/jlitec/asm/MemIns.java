package com.yuntongzhang.jlitec.asm;

public class MemIns extends InsArm {
    private Opcode opcode;
    private Cond cond;
    // omit word type
    private Reg reg;
    private Address address;

    public MemIns(Opcode opcode, Cond cond, Reg reg, Address address) {
        this.opcode = opcode;
        this.cond = cond;
        this.reg = reg;
        this.address = address;
    }

    public MemIns(Opcode opcode, Reg reg, Address address) {
        this(opcode, Cond.AL, reg, address);
    }

    @Override
    public String toString() {
        return opcode.name + cond.toString() + " " + reg.toString() + "," + address.toString();
    }

    public enum Opcode {
        LDR("ldr"),
        STR("str");
        private String name;
        Opcode(String name) {
            this.name = name;
        }
    }
}
