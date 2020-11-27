package com.yuntongzhang.jlitec.asm;

public class MultiplyIns extends InsArm {
    private Cond cond;
    private boolean updateFlag;
    private Reg rd;
    private Reg rn;
    private Reg rm;

    public MultiplyIns(Cond cond, boolean updateFlag, Reg rd, Reg rn, Reg rm) {
        this.cond = cond;
        this.updateFlag = updateFlag;
        this.rd = rd;
        this.rn = rn;
        this.rm = rm;
    }

    public MultiplyIns(Reg rd, Reg rn, Reg rm) {
        this(Cond.AL, false, rd, rn, rm);
    }

    @Override
    public String toString() {
        String updateFlagString = updateFlag ? "S" : "";
        return "mul" + updateFlagString + cond.toString() + " "
                + rd.toString() + "," + rn.toString() + "," + rm.toString();
    }
}
