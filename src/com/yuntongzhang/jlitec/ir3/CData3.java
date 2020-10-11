package com.yuntongzhang.jlitec.ir3;

import java.util.ArrayList;
import java.util.List;

import com.yuntongzhang.jlitec.ast.PrettyPrintable;
import com.yuntongzhang.jlitec.ast.VarDeclaration;

public class CData3 implements PrettyPrintable {
    private String cname;
    private List<VarDecl3> varDecl3List;

    public CData3(String cname, List<VarDeclaration> varDeclarations) {
        this.cname = cname;
        this.varDecl3List = new ArrayList<>();
        for (VarDeclaration varDeclaration : varDeclarations) {
            this.varDecl3List.add(new VarDecl3(varDeclaration));
        }
    }

    @Override
    public void prettyPrint(int indentation) {
        String begin = "Data " + cname + " {".indent(indentation);
        System.out.print(begin);
        for (VarDecl3 varDecl3 : varDecl3List) {
            varDecl3.prettyPrint(indentation + 2);
        }
        String end = "}".indent(indentation);
        System.out.println(end);
    }
}
