package com.yuntongzhang.jlitec.ir3;

import java.util.List;
import java.util.stream.Collectors;

public class MethodCallExp3 extends Exp3 {
    private Id3 methodName;
    private List<Idc3> argList;

    public MethodCallExp3(Id3 methodName, List<Idc3> argList) {
        this.methodName = methodName;
        this.argList = argList;
    }

    @Override
    public String toString() {
        String argListString = argList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        return methodName.toString() + "(" + argListString + ")";
    }
}
