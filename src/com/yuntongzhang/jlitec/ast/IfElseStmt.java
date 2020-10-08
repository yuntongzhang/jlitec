package com.yuntongzhang.jlitec.ast;

import java.util.List;

public class IfElseStmt extends Stmt {
    private Expression condition;
    private List<Stmt> ifBranch;
    private List<Stmt> elseBranch;

    public IfElseStmt(Expression condition, List<Stmt> ifBranch, List<Stmt> elseBranch, Node.Location loc) {
        super(loc);
        this.condition = condition;
        this.ifBranch = ifBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public void prettyPrint(int indentation) {
        String ifLine = ("If(" + condition.toString() + ")").indent(indentation);
        String elseLine = "else".indent(indentation);
        String openBraceLine = "{".indent(indentation);
        String closeBraceLine = "}".indent(indentation);

        System.out.print(ifLine);
        System.out.print(openBraceLine);
        for (Stmt stmt : ifBranch) stmt.prettyPrint(indentation + 2);
        System.out.print(closeBraceLine);
        System.out.print(elseLine);
        System.out.print(openBraceLine);
        for (Stmt stmt : elseBranch) stmt.prettyPrint(indentation + 2);
        System.out.print(closeBraceLine);
    }
}
