package com.yuntongzhang.jlitec.asm;

public class BranchIns extends InsArm {
    // keep some reserved method names which are used in BL instructions
    public static String PRINTF = "printf";
    // use malloc instead of _Znwj, as _Znwj is `new` and extra library needs to be linked
    public static String MALLOC = "malloc";
    // use calloc instead of malloc, since it also initializes the allocated memory to 0s
    public static String CALLOC = "calloc";
    public static String STRLEN = "strlen";
    public static String STRCAT = "strcat";

    private Opcode opcode;
    private Cond cond;
    private LabelArm target;

    public BranchIns(Opcode opcode, Cond cond, LabelArm target) {
        this.opcode = opcode;
        this.cond = cond;
        this.target = target;
    }

    public BranchIns(Opcode opcode, LabelArm target) {
        this(opcode, Cond.AL, target);
    }

    // for BL with method name
    public BranchIns(String methodName) {
        this(Opcode.BL, Cond.AL, new LabelArm(methodName));
    }

    @Override
    public String toString() {
        String result = opcode.name + cond.toString() + " " + target.getName();
        if (opcode == Opcode.BL) {
            result += "(PLT)";
        }
        return result;
    }

    public enum Opcode {
        B("b"),
        BL("bl");
        private String name;

        Opcode(String name) {
            this.name = name;
        }
    }
}
