package com.yuntongzhang.jlitec.ast;

import java.util.List;
import java.util.stream.Collectors;

public class MethodBody extends Node implements PrettyPrintable {
    private List<VarDeclaration> varDeclarations;
    private List<Stmt> stmts;

    public MethodBody(List<VarDeclaration> varDeclarations, List<Stmt> stmts, Node.Location loc) {
        super(loc);
        this.varDeclarations = varDeclarations;
        this.stmts = stmts;
    }

    public List<VarDeclaration> getVarDeclarations() {
        return varDeclarations;
    }

    public List<Stmt> getStmts() {
        return stmts;
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
