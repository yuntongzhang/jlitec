package com.yuntongzhang.jlitec.ast;

import java.util.List;

public class Program implements PrettyPrintable {
    private MainClass mainClass;
    private List<ClassDeclaration> classDeclarations;

    public Program(MainClass mainClass, List<ClassDeclaration> classDeclarations) {
        this.mainClass = mainClass;
        this.classDeclarations = classDeclarations;
    }

    @Override
    public void prettyPrint(int indentation) {
        mainClass.prettyPrint(indentation);
        for (ClassDeclaration classDecl: classDeclarations) {
            classDecl.prettyPrint(indentation);
        }
    }
}
