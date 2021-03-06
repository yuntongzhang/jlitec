package com.yuntongzhang.jlitec.ir3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yuntongzhang.jlitec.ast.PrettyPrintable;
import com.yuntongzhang.jlitec.ast.VarDeclaration;

public class MdBody3 implements PrettyPrintable {
    private List<Stmt3> stmt3List = new ArrayList<>();

    public MdBody3(List<VarDeclaration> varDeclarations, List<Stmt3> stmt3List) {
        this.stmt3List.addAll(genVarDecl3List(varDeclarations));
        this.stmt3List.addAll(eliminateUnusedLabels(stmt3List));
    }

    public MdBody3(List<Stmt3> insList) {
        this.stmt3List = insList;
    }

    /**
     * Returns all stmts, including var declarations.
     */
    public List<Stmt3> getAllStmts() {
        List<Stmt3> result = new ArrayList<>(stmt3List);
        return result;
    }

    private List<VarDecl3> genVarDecl3List(List<VarDeclaration> varDeclarations) {
        List<VarDecl3> result = new ArrayList<>();
        for (VarDeclaration varDeclaration : varDeclarations) {
            result.add(new VarDecl3(varDeclaration));
        }
        return result;
    }

    private List<Stmt3> eliminateUnusedLabels(List<Stmt3> stmt3List) {
        List<Stmt3> result = new ArrayList<>(stmt3List);
        Set<Label3> referencedLabel = new HashSet<>();
        // get all referenced label (used label)
        for (Stmt3 stmt3 : stmt3List) {
            if (stmt3 instanceof IfGoto3) {
                IfGoto3 ifGoto3 = (IfGoto3) stmt3;
                referencedLabel.add(ifGoto3.getLabel());
            }
            if (stmt3 instanceof Goto3) {
                Goto3 goto3 = (Goto3) stmt3;
                referencedLabel.add(goto3.getLabel());
            }
        }
        // remove any unused label
        for (Stmt3 stmt3 : stmt3List) {
            if (stmt3 instanceof Label3) {
                Label3 label3 = (Label3) stmt3;
                if (!referencedLabel.contains(label3)) {
                    result.remove(label3);
                }
            }
        }
        return result;
    }

    @Override
    public void prettyPrint(int indentation) {
        for (Stmt3 stmt3 : stmt3List) {
            stmt3.prettyPrint(indentation);
        }
    }
}
