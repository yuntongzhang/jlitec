package com.yuntongzhang.jlitec.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainClass extends ClassDeclaration implements PrettyPrintable {
    public MainClass(String cname, List<FmlItem> fmlList, MethodBody methodBody,
                     Node.Location loc, Node.Location idLoc, Node.Location methodLoc) {
        super(cname, new ArrayList<VarDeclaration>(),
                constructMethodDeclarations(fmlList, methodBody, idLoc, methodLoc), loc);
    }

    // to facilitate constructor
    private static List<MethodDeclaration> constructMethodDeclarations(List<FmlItem> fmlList, MethodBody methodBody,
                                                                       Node.Location idLoc, Node.Location methodLoc) {
        List<MethodDeclaration> methodDeclarations = new ArrayList<>();
        MethodDeclaration mainMethod = new MethodDeclaration(new SType("Void"),
                new Identifier("main", idLoc), fmlList, methodBody, methodLoc);
        methodDeclarations.add(mainMethod);
        return methodDeclarations;
    }
}
