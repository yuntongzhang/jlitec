package com.yuntongzhang.jlitec.asm;

public abstract class Address {
    public static class LabelAddr extends Address {
        private LabelArm label;
        public LabelAddr(LabelArm label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return "=" + label.getName();
        }
    }

    public static class RegAddr extends Address {
        private Reg reg;
        public RegAddr(Reg reg) {
            this.reg = reg;
        }

        @Override
        public String toString() {
            return reg.toString();
        }
    }

    public static class RegPreIndexed extends Address {
        private Reg reg;
        private int index;
        private boolean writeBack;

        public RegPreIndexed(Reg reg, int index, boolean writeBack) {
            this.reg = reg;
            this.index = index;
            this.writeBack = writeBack;
        }

        // Access from FP/SP or other regs (object start) with no write back
        public RegPreIndexed(Reg reg, int index) {
            this(reg, index, false);
        }

        @Override
        public String toString() {
            String writeBackString = writeBack ? "!" : "";
            return "[" + reg.toString() + ",#" + index + "]" + writeBackString;
        }
    }

    public class RegPostIndexed extends Address {
        private Reg reg;
        private int index;

        public RegPostIndexed(Reg reg, int index) {
            this.reg = reg;
            this.index = index;
        }

        @Override
        public String toString() {
            return "[" + reg.toString() + "],#" + index;
        }
    }
}
