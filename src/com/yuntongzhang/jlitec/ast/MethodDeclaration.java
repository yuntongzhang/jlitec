package com.yuntongzhang.jlitec.ast;

import java.util.List;
import java.util.stream.Collectors;

public class MethodDeclaration implements PrettyPrintable {
    private Type type;
    private Identifier id;
    private List<FmlItem> fmlList;
    private MethodBody methodBody;

    public MethodDeclaration(Type type, Identifier id, List<FmlItem> fmlList, MethodBody methodBody) {
        this.type = type;
        this.id = id;
        this.fmlList = fmlList;
        this.methodBody = methodBody;
    }

    @Override
    public void prettyPrint(int indentation) {
        String fmlListString = fmlList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        String open = (type.toString() + " " + id.toString() + "(" + fmlListString + "){").indent(indentation);
        String close = "}".indent(indentation);

        System.out.print(open);
        methodBody.prettyPrint(indentation + 2);
        System.out.print(close);
        System.out.println();
    }
}
