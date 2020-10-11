package com.yuntongzhang.jlitec.ast;

import java.util.List;
import java.util.stream.Collectors;


public class MethodDeclaration extends Node implements PrettyPrintable {
    private SType returnType;
    private Identifier id;
    private List<FmlItem> fmlList;
    private MethodBody methodBody;
    private FuncType funcType;

    public MethodDeclaration(SType returnType, Identifier id, List<FmlItem> fmlList,
                             MethodBody methodBody, Node.Location loc) {
        super(loc);
        this.returnType = returnType;
        this.id = id;
        this.fmlList = fmlList;
        this.methodBody = methodBody;
        this.funcType = new FuncType(fmlList, returnType);
    }

    public SType getReturnType() {
        return returnType;
    }

    public Identifier getId() {
        return id;
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public MethodBody getMethodBody() {
        return methodBody;
    }

    public List<FmlItem> getFmlList() {
        return fmlList;
    }

    @Override
    public void prettyPrint(int indentation) {
        String fmlListString = fmlList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        String open = (returnType.toString() + " " + id.toString() + "(" + fmlListString + "){").indent(indentation);
        String close = "}".indent(indentation);

        System.out.print(open);
        methodBody.prettyPrint(indentation + 2);
        System.out.print(close);
        System.out.println();
    }
}
