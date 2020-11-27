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
 * Dataflow value: (Id3,Exp3) (need to make sure all Exp3 has `equals` and `hashcode` properly defined
 * At each program point, we want to know the set of available expressions and what var they are assigned to.
 *
 * Note: For Exp3, we exclude Id3 and literals in this analysis,
 *       because they dont get eliminated in subsequent CSE.
 *
 * We also exclude the complication of having object.field as either LHS or RHS, that is:
 *  (1) Access3 is not considered as a valid Exp3 in dataflow value
 *  (2) AssignAccess3 does not introduce new dataflow value or invalidate any existing dataflow value
 */
public class AvailableExpAnalyzer extends DataflowAnalyzer<AvailableExpAnalyzer.Assignment> {
    private static BiFunction<Stmt3, Set<Assignment>, Set<Assignment>> transferFunction = (ins, input) -> {
        Set<Assignment> output = new HashSet<>(input);
        output.addAll(ins.getAssignment());
        Id3 definedVar;
        if (ins instanceof AssignId3) {
            definedVar = ((AssignId3) ins).getLhs();
        } else if (ins instanceof AssignDeclare3) {
            definedVar = ((AssignDeclare3) ins).getLhs();
        } else {
            return output;
        }
        // ins defined some var
        Set<Assignment> killedSet = new HashSet<>();
        for (Assignment assignment : output) {
            if (assignment.containsVarAtRhs(definedVar)) killedSet.add(assignment);
        }
        output.removeAll(killedSet);
        return output;
    };

    public AvailableExpAnalyzer(FlowGraph flowGraph) {
        super(flowGraph, transferFunction, true, false,
                new HashSet<>(), getAllAssignmentsFromCFG(flowGraph));
    }

    // to set the initial value to the all possible dataflow values
    private static Set<Assignment> getAllAssignmentsFromCFG(FlowGraph flowGraph) {
        Set<Assignment> result = new HashSet<>();
        for (BasicBlock basicBlock : flowGraph.getAllBbs()) {
            for (Stmt3 stmt3 : basicBlock.getInsList()) {
                result.addAll(stmt3.getAssignment());
            }
        }
        return result;
    }

    public static class Assignment {
        private Id3 var;
        private Exp3 exp;

        public Assignment(Id3 var, Exp3 exp) {
            this.var = var;
            this.exp = exp;
        }

        public boolean containsVarAtRhs(Id3 someVar) {
            return exp.containsVar(someVar);
        }

        public boolean isForExp(Exp3 someExp) {
            return exp.equals(someExp);
        }

        public Id3 getVar() {
            return this.var;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Assignment that = (Assignment) o;
            return var.equals(that.var) &&
                    exp.equals(that.exp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(var, exp);
        }
    }
}
