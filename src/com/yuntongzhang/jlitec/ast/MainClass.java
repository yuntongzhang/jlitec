package com.yuntongzhang.jlitec.ast;

import java.util.List;
import java.util.stream.Collectors;

public class MainClass implements PrettyPrintable{
    private String cname;
    private List<FmlItem> fmlList;
    private MethodBody methodBody;

    public MainClass(String cname, List<FmlItem> fmlList, MethodBody methodBody) {
        this.cname = cname;
        this.fmlList = fmlList;
        this.methodBody = methodBody;
    }

    @Override
    public void prettyPrint(int indentation) {
        String one = ("class " + cname + "{").indent(indentation);
        String fmlListString = fmlList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        String two = ("Void main(" + fmlListString + "){").indent(indentation + 2);
        String three = "}".indent(indentation + 2);
        String four = "}".indent(indentation);

        System.out.print(one);
        System.out.print(two);
        methodBody.prettyPrint(indentation + 4);
        System.out.print(three);
        System.out.print(four);
        System.out.println();
    }
}
