package com.yuntongzhang.jlitec.asm;

import java.util.ArrayList;
import java.util.List;

public class TextSection {
    private List<MethodBlock> blocks;

    public TextSection() {
        this.blocks = new ArrayList<>();
    }

    public void addMethod(MethodBlock methodBlock) {
        this.blocks.add(methodBlock);
    }

    public void prettyPrint() {
        System.out.println(".text");
        System.out.println(".global main");
        for (MethodBlock methodBlock : blocks) {
            methodBlock.prettyPrint();
        }
    }
}
