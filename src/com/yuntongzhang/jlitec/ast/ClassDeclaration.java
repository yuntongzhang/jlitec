package com.yuntongzhang.jlitec.ast;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.yuntongzhang.jlitec.exceptions.NonDistinctNameError;

public class ClassDeclaration extends Node implements PrettyPrintable {
    private String cname;
    private List<VarDeclaration> varDeclarations;
    private List<MethodDeclaration> methodDeclarations;

    public ClassDeclaration(String cname, List<VarDeclaration> varDeclarations,
                            List<MethodDeclaration> methodDeclarations, Node.Location loc) {
        super(loc);
        this.cname = cname;
        this.varDeclarations = varDeclarations;
        this.methodDeclarations = methodDeclarations;
    }

    public String getCname() {
        return cname;
    }

    public void distinctNameCheck() throws NonDistinctNameError {
        List<String> varNames = varDeclarations.stream().map(v -> v.getId().getName()).collect(Collectors.toList());
        // check all fields in a class have distinct names
        if (varNames.size() != new HashSet<>(varNames).size()) {
            throw new NonDistinctNameError("Class has fields with duplicated names!", this.loc);
        }

        List<String> methodNames = methodDeclarations.stream().map(m -> m.getId().getName()).collect(Collectors.toList());
        // check all methods in a class have distinct names
        // TODO: allow method overloading
        if (methodNames.size() != new HashSet<>(methodNames).size()) {
            throw new NonDistinctNameError("Class has methods with duplicated names!", this.loc);
        }
        for (MethodDeclaration mdecl : methodDeclarations) {
            mdecl.distinctNameCheck();
        }
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
