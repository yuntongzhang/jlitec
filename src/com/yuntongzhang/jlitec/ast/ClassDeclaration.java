package com.yuntongzhang.jlitec.ast;

import java.util.List;
import java.util.stream.Collectors;


public class ClassDeclaration extends Node implements PrettyPrintable {
    private String cname;
    private SType type;
    private List<VarDeclaration> varDeclarations;
    private List<MethodDeclaration> methodDeclarations;

    public ClassDeclaration(String cname, List<VarDeclaration> varDeclarations,
                            List<MethodDeclaration> methodDeclarations, Node.Location loc) {
        super(loc);
        this.cname = cname;
        this.type = new SType(cname);
        this.varDeclarations = varDeclarations;
        this.methodDeclarations = methodDeclarations;
    }

    public String getCname() {
        return cname;
    }

    public SType getType() {
        return type;
    }

    public List<VarDeclaration> getVarDeclarations() {
        return varDeclarations;
    }

    public List<MethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }

    @Override
    public void prettyPrint(int indentation) {
        String open = ("class " + cname + "{").indent(indentation);
        String close = "}".indent(indentation);
        String varDeclStrings = varDeclarations.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining("\n"))
                .indent(indentation + 2);

        System.out.print(open);
        System.out.print(varDeclStrings);
        System.out.println();
        for (MethodDeclaration methodDecl : methodDeclarations) {
            methodDecl.prettyPrint(indentation + 2);
        }
        System.out.print(close);
        System.out.println();
    }
}
