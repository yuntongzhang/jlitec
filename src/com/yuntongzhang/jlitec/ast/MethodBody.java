package com.yuntongzhang.jlitec.ast;

import java.util.List;
import java.util.stream.Collectors;

public class MethodBody implements PrettyPrintable {
    private List<VarDeclaration> varDeclarations;
    private List<Stmt> stmts;

    public MethodBody(List<VarDeclaration> varDeclarations, List<Stmt> stmts) {
        this.varDeclarations = varDeclarations;
        this.stmts = stmts;
    }

    @Override
    public void prettyPrint(int indentation) {
        String varDeclStrings = varDeclarations.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining("\n"))
                .indent(indentation);

        System.out.print(varDeclStrings);
        for (Stmt stmt: stmts) {
            stmt.prettyPrint(indentation);
        }
    }
}
