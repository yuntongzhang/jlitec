package com.yuntongzhang.jlitec.ast;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.yuntongzhang.jlitec.exceptions.NonDistinctNameError;

public class MethodDeclaration extends Node implements PrettyPrintable {
    private Type type;
    private Identifier id;
    private List<FmlItem> fmlList;
    private MethodBody methodBody;

    public MethodDeclaration(Type type, Identifier id, List<FmlItem> fmlList, MethodBody methodBody, Node.Location loc) {
        super(loc);
        this.type = type;
        this.id = id;
        this.fmlList = fmlList;
        this.methodBody = methodBody;
    }

    public Identifier getId() {
        return id;
    }

    public void distinctNameCheck() throws NonDistinctNameError {
        List<String> fmlNames = fmlList.stream().map(f -> f.getId().getName()).collect(Collectors.toList());
        // check all parameters in method declaration have distinct names
        if (fmlNames.size() != new HashSet<>(fmlNames).size()) {
            throw new NonDistinctNameError("Method declaration has parameters with duplicated names!", this.loc);
        }
    }

    @Override
    public void prettyPrint(int indentation) {
        String fmlListString = fmlList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        String open = (type.toString() + " " + id.toString() + "(" + fmlListString + "){").indent(indentation);
        String close = "}".indent(indentation);

        System.out.print(open);
        methodBody.prettyPrint(indentation + 2);
        System.out.print(close);
        System.out.println();
    }
}
