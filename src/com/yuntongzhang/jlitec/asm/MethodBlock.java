package com.yuntongzhang.jlitec.asm;

import java.util.ArrayList;
import java.util.List;

public class MethodBlock {
    private List<InsArm> insList;

    public MethodBlock() {
        this.insList = new ArrayList<>();
    }

    public void appendIns(InsArm ins) {
        this.insList.add(ins);
    }

    public void appendInsList(List<InsArm> listToAppend) {
        this.insList.addAll(listToAppend);
    }

    public void prettyPrint() {
        for (InsArm ins : insList) {
            System.out.println(ins);
        }
    }
}
