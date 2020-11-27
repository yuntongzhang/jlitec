package com.yuntongzhang.jlitec.asm;

public class ProgramArm {
    private DataSection dataSection;
    private TextSection textSection;

    public ProgramArm(DataSection dataSection, TextSection textSection) {
        this.dataSection = dataSection;
        this.textSection = textSection;
    }

    public void prettyPrint() {
        dataSection.prettyPrint();
        textSection.prettyPrint();
    }
}
