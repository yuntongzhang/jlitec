package com.yuntongzhang.jlitec.asm;

public abstract class Operand2 {
    public static class ImmedOp extends Operand2 {
        private int val;

        public ImmedOp(int val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return "#" + val;
        }
    }

    public static class RegOp extends Operand2 {
        private Reg val;

        public RegOp(Reg val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return val.toString();
        }
    }
}
