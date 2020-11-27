package com.yuntongzhang.jlitec.analysis;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import com.yuntongzhang.jlitec.ir3.AssignDeclare3;
import com.yuntongzhang.jlitec.ir3.AssignId3;
import com.yuntongzhang.jlitec.ir3.Exp3;
import com.yuntongzhang.jlitec.ir3.Id3;
import com.yuntongzhang.jlitec.ir3.Stmt3;

/**
 * Dataflow value is copy statement with the form of `x = y`,
 *      where both x and y are of type Id3.
 */
public class CopyPropAnalyzer extends DataflowAnalyzer<CopyPropAnalyzer.Copy> {
    private static BiFunction<Stmt3, Set<Copy>, Set<Copy>> transferFunction = (ins, input) -> {
        // If ins is AssignId3 or AssignDeclare3, it kills some copy
        // additionally if they are `x=y`, they gens new copy
        Set<Copy> output = new HashSet<>(input);
        Id3 left;
        Exp3 right;
        if (ins instanceof AssignId3) {
            AssignId3 assignId3 = (AssignId3) ins;
            left = assignId3.getLhs();
            right = assignId3.getRhs();
        } else if (ins instanceof AssignDeclare3) {
            AssignDeclare3 assignDeclare3 = (AssignDeclare3) ins;
            left = assignDeclare3.getLhs();
            right = assignDeclare3.getRhs();
        } else { // other stmts cannot be copy
            return output;
        }
        // ins kills some copy
        Set<Copy> killSet = new HashSet<>();
        for (Copy copy : input) {
            if (copy.left.equals(left) || copy.right.equals(left)) {
                killSet.add(copy);
            }
        }
        output.removeAll(killSet);
        // ins can also gen some copy
        if (right instanceof Id3) {
            Id3 rightId = (Id3) right;
            output.add(new Copy(left, rightId));
        }
        return output;
    };

    public CopyPropAnalyzer(FlowGraph flowGraph) {
        super(flowGraph, transferFunction, true, false,
                new HashSet<>(), getAllCopyFromCFG(flowGraph));
    }

    private static Set<Copy> getAllCopyFromCFG(FlowGraph flowGraph) {
        Set<Copy> result = new HashSet<>();
        for (BasicBlock basicBlock : flowGraph.getAllBbs()) {
            for (Stmt3 stmt3 : basicBlock.getInsList()) {
                if (stmt3 instanceof AssignDeclare3) {
                    AssignDeclare3 assignDeclare3 = (AssignDeclare3) stmt3;
                    Exp3 right = assignDeclare3.getRhs();
                    if (right instanceof Id3) {
                        result.add(new Copy(assignDeclare3.getLhs(), (Id3) right));
                    }
                }
                if (stmt3 instanceof AssignId3) {
                    AssignId3 assignDeclare3 = (AssignId3) stmt3;
                    Exp3 right = assignDeclare3.getRhs();
                    if (right instanceof Id3) {
                        result.add(new Copy(assignDeclare3.getLhs(), (Id3) right));
                    }
                }
            }
        }
        return result;
    }

    public static class Copy {
        Id3 left;
        Id3 right;

        public Copy(Id3 left, Id3 right) {
            this.left = left;
            this.right = right;
        }

        public Id3 getLeft() {
            return left;
        }

        public Id3 getRight() {
            return right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Copy copy = (Copy) o;
            return left.equals(copy.left) &&
                    right.equals(copy.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }
    }
}
