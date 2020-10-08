package com.yuntongzhang.jlitec.ast;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.yuntongzhang.jlitec.exceptions.NonDistinctNameError;

public class Program extends Node implements PrettyPrintable {
    private MainClass mainClass;
    private List<ClassDeclaration> classDeclarations;

    public Program(MainClass mainClass, List<ClassDeclaration> classDeclarations, Node.Location loc) {
        super(loc);
        this.mainClass = mainClass;
        this.classDeclarations = classDeclarations;
    }

    // entry for all the distinct name checks
    public void distinctNameCheck() throws NonDistinctNameError {
        List<String> classNames = classDeclarations.stream().map(d -> d.getCname()).collect(Collectors.toList());
        // check whether all class names are distinct
        if (classNames.size() != new HashSet<>(classNames).size()) {
            throw new NonDistinctNameError("Program has classes with duplicated names!", this.loc);
        }
        for (ClassDeclaration cdecl : classDeclarations) {
            cdecl.distinctNameCheck();
        }
    }

    @Override
    public void prettyPrint(int indentation) {
        mainClass.prettyPrint(indentation);
        for (ClassDeclaration classDecl: classDeclarations) {
            classDecl.prettyPrint(indentation);
        }
    }
}
