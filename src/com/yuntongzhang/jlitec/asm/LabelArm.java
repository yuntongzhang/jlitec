package com.yuntongzhang.jlitec.asm;

import com.yuntongzhang.jlitec.ir3.Label3;

/**
 * Include labels for data, methods, and jump targets
 */
public class LabelArm extends InsArm {
    private static int methodCounter = 1;
    private static int dataCounter = 1;

    private String name;

    public LabelArm(String name) {
        this.name = name;
    }

    // for jump targets
    public LabelArm(Label3 label3) {
        this.name = "." + label3.getNumber();
    }

    // for method exit
    public static LabelArm genNewEpilogueLabel() {
        LabelArm result = new LabelArm(".M" + methodCounter + "exit");
        methodCounter++;
        return result;
    }

    // for data
    public static LabelArm genNewDataLabel() {
        LabelArm result = new LabelArm("L" + dataCounter);
        dataCounter++;
        return result;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "\n" + name + ":";
    }
}
