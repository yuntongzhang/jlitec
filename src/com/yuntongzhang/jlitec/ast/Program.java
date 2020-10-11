package com.yuntongzhang.jlitec.ast;

import java.util.List;


public class Program extends Node implements PrettyPrintable {
    private MainClass mainClass;
    private List<ClassDeclaration> classDeclarations;

    public Program(MainClass mainClass, List<ClassDeclaration> classDeclarations, Node.Location loc) {
        super(loc);
        this.mainClass = mainClass;
        this.classDeclarations = classDeclarations;
    }

    public MainClass getMainClass() {
        return mainClass;
    }

    public List<ClassDeclaration> getClassDeclarations() {
        return classDeclarations;
    }

    @Override
    public void prettyPrint(int indentation) {
        mainClass.prettyPrint(indentation);
        for (ClassDeclaration classDecl: classDeclarations) {
            classDecl.prettyPrint(indentation);
        }
    }
}
