package com.yuntongzhang.jlitec.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.yuntongzhang.jlitec.analysis.AvailableExpAnalyzer;
import com.yuntongzhang.jlitec.analysis.CopyPropAnalyzer;
import com.yuntongzhang.jlitec.analysis.FlowGraph;
import com.yuntongzhang.jlitec.analysis.LivenessAnalyzer;
import com.yuntongzhang.jlitec.ir3.AssignDeclare3;
import com.yuntongzhang.jlitec.ir3.AssignId3;
import com.yuntongzhang.jlitec.ir3.CData3;
import com.yuntongzhang.jlitec.ir3.CMtd3;
import com.yuntongzhang.jlitec.ir3.Exp3;
import com.yuntongzhang.jlitec.ir3.Id3;
import com.yuntongzhang.jlitec.ir3.MdBody3;
import com.yuntongzhang.jlitec.ir3.Program3;
import com.yuntongzhang.jlitec.ir3.Stmt3;

public class IrOptimizer {
    private List<CData3> dataList;
    private List<CMtd3> methodList;
    // keep track of whether any optimization has been done to the current method
    private boolean methodChanged = false;

    public IrOptimizer(Program3 program3) {
        this.dataList = program3.getDataList();
        this.methodList = program3.getMethodList();
    }

    // mutate the underlying program
    public void run() {
        for (CMtd3 cMtd3 : methodList) {
            List<Stmt3> insList = cMtd3.getMethodBody().getAllStmts();
            List<Stmt3> updatedInsList = optimizeMethod(insList);
            MdBody3 newMdBody = new MdBody3(updatedInsList);
            cMtd3.setMethodBody(newMdBody);
        }
    }

    private List<Stmt3> optimizeMethod(List<Stmt3> insList) {
        List<Stmt3> outputCode = new ArrayList<>(insList);
        methodChanged = true;
        // we limit the maximum round of performing these optimizations
        // this is to prevent potential error case where optimization stucks and goes on forever
        int currentRound = 0;
        while (methodChanged) {
            if (currentRound > 20) break;
            methodChanged = false;
            // step (1) Common Subexpression Elimination
            outputCode = commonSubexpressionElimination(outputCode);
            // step (2) copy propagation
            outputCode = copyPropagation(outputCode);
            // step (3) dead code elimination
            outputCode = deadCodeElimination(outputCode);
            currentRound++;
        }
        return outputCode;
    }


    private List<Stmt3> deadCodeElimination(List<Stmt3> insList) {
        FlowGraph flowGraph = new FlowGraph(insList);
        List<Stmt3> updatedCode = new ArrayList<>(insList);
        LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer(flowGraph);
        livenessAnalyzer.run();
        Map<Stmt3, Set<Id3>> outResult = livenessAnalyzer.getOutMap();

        for (Map.Entry<Stmt3, Set<Id3>> entry : outResult.entrySet()) {
            Stmt3 stmt = entry.getKey();
            Set<Id3> liveVars = entry.getValue();
            Id3 left;
            if (stmt instanceof AssignId3) {
                left = ((AssignId3) stmt).getLhs();
            } else if (stmt instanceof AssignDeclare3) {
                left = ((AssignDeclare3) stmt).getLhs();
            } else {
                continue;
            }
            // we only care about redundant assignments
            // that is, an assignment stmt whose defined var is not live immediately after it
            if (liveVars.contains(left)) continue;
            // left is not live
            updatedCode.remove(stmt);
            methodChanged = true;
        }
        return updatedCode;
    }

    private List<Stmt3> copyPropagation(List<Stmt3> insList) {
        FlowGraph flowGraph = new FlowGraph(insList);
        List<Stmt3> updatedCode = new ArrayList<>(insList);
        CopyPropAnalyzer copyPropAnalyzer = new CopyPropAnalyzer(flowGraph);
        copyPropAnalyzer.run();
        Map<Stmt3, Set<CopyPropAnalyzer.Copy>> inResult = copyPropAnalyzer.getInMap();

        for (Map.Entry<Stmt3, Set<CopyPropAnalyzer.Copy>> entry : inResult.entrySet()) {
            Stmt3 stmt = entry.getKey();
            Set<Id3> useSet = stmt.getUseSet();
            Set<CopyPropAnalyzer.Copy> reachedCopies = entry.getValue();
            // check if any reached copy can be used for any used var in this ins
            Map<Id3, Id3> varReplaceScheme = new HashMap<>();
            for (Id3 usedVar : useSet) {
                for (CopyPropAnalyzer.Copy copy : reachedCopies) {
                    if (copy.getLeft().equals(usedVar)) {
                        varReplaceScheme.put(usedVar, copy.getRight());
                    }
                }
            }
            if (varReplaceScheme.isEmpty()) continue;
            // find this stmt in updatedCode, and apply the var replacement scheme
            Stmt3 stmtToBeUpdated = updatedCode.get(updatedCode.indexOf(stmt));
            stmtToBeUpdated.replaceVars(varReplaceScheme);
            methodChanged = true;
        }
        return updatedCode;
    }


    private List<Stmt3> commonSubexpressionElimination(List<Stmt3> insList) {
        FlowGraph flowGraph = new FlowGraph(insList);
        List<Stmt3> updatedCode = new ArrayList<>(insList);
        AvailableExpAnalyzer availableExpAnalyzer = new AvailableExpAnalyzer(flowGraph);
        availableExpAnalyzer.run();
        Map<Stmt3, Set<AvailableExpAnalyzer.Assignment>> inResult = availableExpAnalyzer.getInMap();

        for (Map.Entry<Stmt3, Set<AvailableExpAnalyzer.Assignment>> entry : inResult.entrySet()) {
            Stmt3 stmt = entry.getKey();
            Exp3 rhsExp;
            if (stmt instanceof AssignDeclare3) {
                rhsExp = ((AssignDeclare3) stmt).getRhs();
            } else if (stmt instanceof AssignId3) {
                rhsExp = ((AssignId3) stmt).getRhs();
            } else {
                // only consider assign statements here
                // possible to include other stmt types as well,
                // but assign stmts are of greatest importance,
                // since they are related to copy propagation and DCE later on
                continue;
            }
            Set<AvailableExpAnalyzer.Assignment> availableAssignments = entry.getValue();
            Set<AvailableExpAnalyzer.Assignment> suitableAssignments = availableAssignments.stream()
                    .filter(assignment -> assignment.isForExp(rhsExp))
                    .collect(Collectors.toSet());
            if (suitableAssignments.size() != 1) {
                // skip if no available expression is same as rhs
                // skip also when more than one available expressions are same as rhs
                //     to avoid the complexity of introducing new common variable to predecessor blocks
                continue;
            }
            // we are ready to perform CSE to this stmt
            Stmt3 stmtToBeUpdated = updatedCode.get(updatedCode.indexOf(stmt));
            Id3 newExp = new ArrayList<>(suitableAssignments).get(0).getVar();
            if (stmtToBeUpdated instanceof AssignDeclare3) {
                ((AssignDeclare3) stmtToBeUpdated).setRhs(newExp);
            }
            if (stmtToBeUpdated instanceof AssignId3) {
                ((AssignId3) stmtToBeUpdated).setRhs(newExp);
            }
            methodChanged = true;
        }
        return updatedCode;
    }
}
