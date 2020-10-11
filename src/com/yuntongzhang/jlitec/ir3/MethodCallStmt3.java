package com.yuntongzhang.jlitec.ir3;

import java.util.List;
import java.util.stream.Collectors;

public class MethodCallStmt3 extends Stmt3 {
    private Id3 methodName;
    private List<Idc3> argList;

    public MethodCallStmt3(Id3 methodName, List<Idc3> argList) {
        this.methodName = methodName;
        this.argList = argList;
    }

    @Override
    public void prettyPrint(int indentation) {
        String argListString = argList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        String toPrint = (methodName.toString() + "(" + argListString + ");").indent(indentation);
        System.out.print(toPrint);
    }
}
