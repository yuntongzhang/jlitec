package com.yuntongzhang.jlitec.check;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.yuntongzhang.jlitec.ast.ClassDeclaration;
import com.yuntongzhang.jlitec.ast.FmlItem;
import com.yuntongzhang.jlitec.ast.MethodDeclaration;
import com.yuntongzhang.jlitec.ast.Program;
import com.yuntongzhang.jlitec.ast.VarDeclaration;
import com.yuntongzhang.jlitec.exceptions.NonDistinctNameError;

public class DistinctNameChecker {
    Program program;

    public DistinctNameChecker(Program program) {
        this.program = program;
    }

    public void check() throws NonDistinctNameError {
        List<ClassDeclaration> classDeclarations = program.getClassDeclarations();
        List<String> classNames = classDeclarations.stream().map(d -> d.getCname()).collect(Collectors.toList());
        // check whether all class names are distinct
        if (classNames.size() != new HashSet<>(classNames).size()) {
            throw new NonDistinctNameError("Program has classes with duplicated names!", program.getLoc());
        }
        for (ClassDeclaration cdecl : classDeclarations) {
            checkClassDeclaration(cdecl);
        }
    }

    private void checkClassDeclaration(ClassDeclaration classDeclaration) throws NonDistinctNameError {
        List<VarDeclaration> varDeclarations = classDeclaration.getVarDeclarations();
        List<String> varNames = varDeclarations.stream().map(v -> v.getId().getName()).collect(Collectors.toList());
        // check all fields in a class have distinct names
        if (varNames.size() != new HashSet<>(varNames).size()) {
            throw new NonDistinctNameError("Class has fields with duplicated names!", classDeclaration.getLoc());
        }

        List<MethodDeclaration> methodDeclarations = classDeclaration.getMethodDeclarations();
        List<String> methodNames = methodDeclarations.stream().map(m -> m.getId().getName()).collect(Collectors.toList());
        // check all methods in a class have distinct names
        if (methodNames.size() != new HashSet<>(methodNames).size()) {
            throw new NonDistinctNameError("Class has methods with duplicated names!", classDeclaration.getLoc());
        }
        for (MethodDeclaration mdecl : methodDeclarations) {
            checkMethodDeclaration(mdecl);
        }
    }

    private void checkMethodDeclaration(MethodDeclaration methodDeclaration) throws NonDistinctNameError {
        List<FmlItem> fmlList = methodDeclaration.getFmlList();
        List<String> fmlNames = fmlList.stream().map(f -> f.getId().getName()).collect(Collectors.toList());
        // check all parameters in method declaration have distinct names
        if (fmlNames.size() != new HashSet<>(fmlNames).size()) {
            throw new NonDistinctNameError("Method declaration has parameters with duplicated names!",
                    methodDeclaration.getLoc());
        }
    }
}
