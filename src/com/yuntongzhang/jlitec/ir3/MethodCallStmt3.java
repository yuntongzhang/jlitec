package com.yuntongzhang.jlitec.ir3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodCallStmt3 extends Stmt3 {
    private Id3 methodName;
    private List<Idc3> argList;

    public MethodCallStmt3(Id3 methodName, List<Idc3> argList) {
        this.methodName = methodName;
        this.argList = argList;
    }

    public Id3 getMethodName() {
        return methodName;
    }

    public List<Idc3> getArgList() {
        return argList;
    }

    @Override
    public Set<Id3> getUseSet() {
        Set<Id3> result = new HashSet<>();
        for (Idc3 arg : argList) {
            if (arg instanceof Id3) {
                result.add((Id3) arg);
            }
        }
        return result;
    }

    @Override
    public void replaceVars(Map<Id3, Id3> replacementScheme) {
        List<Idc3> newArgList = new ArrayList<>();
        for (Idc3 arg : argList) {
            if (arg instanceof Id3 && replacementScheme.containsKey(arg)) {
                newArgList.add(replacementScheme.get(arg));
            } else {
                newArgList.add(arg);
            }
        }
        this.argList = newArgList;
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
