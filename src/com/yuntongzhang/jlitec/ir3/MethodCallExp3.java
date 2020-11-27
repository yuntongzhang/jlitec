package com.yuntongzhang.jlitec.ir3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodCallExp3 extends Exp3 {
    private Id3 methodName;
    private List<Idc3> argList;

    public MethodCallExp3(Id3 methodName, List<Idc3> argList) {
        this.methodName = methodName;
        this.argList = argList;
    }

    public Id3 getMethodName() {
        return methodName;
    }

    public List<Idc3> getArgList() {
        return argList;
    }

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
    public Set<Id3> getAllVariables() {
        Set<Id3> result = new HashSet<>();
        for (Idc3 arg : argList) {
            if (arg instanceof Id3) {
                result.add((Id3) arg);
            }
        }
        return result;
    }

    @Override
    public boolean containsVar(Id3 id3) {
        boolean result = false;
        for (Idc3 arg : argList) {
            if (id3.equals(arg)) result = true;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodCallExp3 that = (MethodCallExp3) o;
        return methodName.equals(that.methodName) &&
                argList.equals(that.argList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, argList);
    }

    @Override
    public String toString() {
        String argListString = argList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        return methodName.toString() + "(" + argListString + ")";
    }
}
