package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.PrettyPrintable;
import com.yuntongzhang.jlitec.ast.VarDeclaration;

public class VarDecl3 implements PrettyPrintable {
    private Type3 type;
    private Id3 id;

    public VarDecl3(VarDeclaration varDeclaration) {
        this.type = new Type3(varDeclaration.getType());
        this.id = new Id3(varDeclaration.getId());
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = (type.toString() + " " + id.toString() + ";").indent(indentation);
        System.out.print(toPrint);
    }
}
