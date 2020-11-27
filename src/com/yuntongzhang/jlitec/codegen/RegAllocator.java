package com.yuntongzhang.jlitec.codegen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yuntongzhang.jlitec.analysis.FlowGraph;
import com.yuntongzhang.jlitec.analysis.LivenessAnalyzer;
import com.yuntongzhang.jlitec.asm.Reg;
import com.yuntongzhang.jlitec.ir3.Id3;
import com.yuntongzhang.jlitec.ir3.Load3;
import com.yuntongzhang.jlitec.ir3.Stmt3;
import com.yuntongzhang.jlitec.ir3.Store3;

/**
 * Performs register allocation on method level.
 * Implements graph coloring register allocation algorithm.
 */
public class RegAllocator {
    private List<Stmt3> codeSequence;
    private Map<Id3, Set<Id3>> interferenceGraph = new HashMap<>();
    List<Reg> availableRegs = Reg.allAllocatableRegs();
    // holds the result of allocation
    private Map<Id3, Reg> varToReg = new HashMap<>();
    // record the spilled vars so far, used for preventing choose a previously spilled var as spill candidate
    Set<Id3> globalSpilledVars = new HashSet<>();

    public RegAllocator(List<Stmt3> codeSequence) {
        this.codeSequence = codeSequence;
    }

    // entry point of allocator
    public void runAllocation() {
        // clear possible old interference graph
        interferenceGraph = new HashMap<>();
        LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer(new FlowGraph(codeSequence));
        livenessAnalyzer.run();
        List<Set<Id3>> liveRangeOverlaps = livenessAnalyzer.getLiveRangeOverlaps();
        Set<Id3> allVarsInMethod = livenessAnalyzer.getAllVariablesInMethod();

        buildInterferenceGraph(liveRangeOverlaps, allVarsInMethod);
        runGraphColoring();
    }

    public Map<Id3, Reg> getAllocationResult() {
        return this.varToReg;
    }

    // if there are spills, new code are inserted.
    // use this method to get up-to-date code sequence.
    public List<Stmt3> getUpdatedCodeSequence() {
        return this.codeSequence;
    }

    // get spilled vars in the updated code sequence
    public List<Id3> getSpilledVars() {
        List<Id3> result = new ArrayList<>();
        for (Stmt3 stmt : this.codeSequence) {
            if (stmt instanceof Store3) {
                Id3 spilledVar = ((Store3) stmt).getVar();
                result.add(spilledVar);
            }
        }
        return result;
    }

    private void buildInterferenceGraph(List<Set<Id3>> liveRangeOverlaps, Set<Id3> allVars) {
        for (Id3 var : allVars) {
            interferenceGraph.put(var, new HashSet<>());
        }
        for (Set<Id3> overlap : liveRangeOverlaps) {
            for (Id3 var : overlap) {
                for (Id3 otherVar : overlap) {
                    if (var == otherVar) continue;
                    interferenceGraph.get(var).add(otherVar);
                    interferenceGraph.get(otherVar).add(var);
                }
            }
        }
    }

    private void runGraphColoring() {
        int numberOfAvailableRegs = availableRegs.size();
        Deque<Id3> nodeStack = new ArrayDeque<>();
        Deque<Set<Id3>> edgeStack = new ArrayDeque<>();
        List<Id3> spillList = new ArrayList<>();

        while (!interferenceGraph.isEmpty()) {
            Id3 nodeToRemove = null;
            // try to simplify
            for (Id3 key : interferenceGraph.keySet()) {
                if (interferenceGraph.get(key).size() < numberOfAvailableRegs) {
                    nodeToRemove = key;
                    break;
                }
            }
            // check if selection is successful
            if (nodeToRemove != null) { // selected one node with degree < k
                // push this node and its edges to stack
                nodeStack.push(nodeToRemove);
                edgeStack.push(interferenceGraph.get(nodeToRemove));
            } else { // need to spill
                int maxDegree = 0;
                // decide which register to spill:
                // (1) previously spilled vars should not be selected again
                // (2) choose the node with max degree
                // select the node with max degree to spill
                for (Id3 key : interferenceGraph.keySet()) {
                    if (globalSpilledVars.contains(key)) continue;
                    int currentDegree = interferenceGraph.get(key).size();
                    if (currentDegree > maxDegree) {
                        maxDegree = currentDegree;
                        nodeToRemove = key;
                    }
                }
                // nodeToRemove should be non-null, and it holds the spill candidate
                spillList.add(nodeToRemove);
                globalSpilledVars.add(nodeToRemove);
            }

            // remove selected/spilled node and its edges from graph
            interferenceGraph.remove(nodeToRemove);
            for (Id3 key : interferenceGraph.keySet()) {
                Set<Id3> value = interferenceGraph.get(key);
                value.remove(nodeToRemove);
            }
        }

        // at this point, graph is empty but spill list can have nodes
        if (!spillList.isEmpty()) {
            insertSpillCode(spillList);
            runAllocation();
        } else { // no more spill, start coloring
            // initialize the graph keys for convenience
            for (Id3 node : nodeStack) {
                interferenceGraph.put(node, new HashSet<>());
            }
            // select node one by one and assign color
            while (!nodeStack.isEmpty()) {
                Id3 nodeToAdd = nodeStack.pop();
                Set<Id3> edgesToAdd = edgeStack.pop();
                // add this node and its edges back
                interferenceGraph.get(nodeToAdd).addAll(edgesToAdd);
                for (Id3 neighbours : edgesToAdd) {
                    interferenceGraph.get(neighbours).add(nodeToAdd);
                }
                color(nodeToAdd);
            }
        }
    }

    // given an instance of interference graph
    // colors a given node, provided that other nodes in the graph are colored
    private void color(Id3 node) {
        Set<Reg> colorsOfNeighbours = new HashSet<>();
        for (Id3 neighbour : interferenceGraph.get(node)) {
            if (varToReg.containsKey(neighbour)) {
                colorsOfNeighbours.add(varToReg.get(neighbour));
            }
        }
        // iterate the available regs in order to pick the one with lowest possible order
        for (Reg candidateColor : availableRegs) {
            if (!colorsOfNeighbours.contains(candidateColor)) {
                varToReg.put(node, candidateColor);
                return;
            }
        }
    }

    private void insertSpillCode(List<Id3> spillList) {
        for (Id3 var : spillList) {
            // insert load and store instructions to code sequence
            List<Stmt3> insListWithDefOfVar = new ArrayList<>();
            List<Stmt3> insListWithUseOfVar = new ArrayList<>();
            for (Stmt3 ins : this.codeSequence) {
                if (ins instanceof Load3 || ins instanceof Store3) {
                    continue;
                }
                if (ins.getDefSet().contains(var)) {
                    insListWithDefOfVar.add(ins);
                }
                if (ins.getUseSet().contains(var)) {
                    insListWithUseOfVar.add(ins);
                }
            }

            for (Stmt3 insWithDefOfVar : insListWithDefOfVar) {
                int index = codeSequence.indexOf(insWithDefOfVar);
                codeSequence.add(index + 1, new Store3(var));
            }
            for (Stmt3 insWithUseOfVar : insListWithUseOfVar) {
                int index = codeSequence.indexOf(insWithUseOfVar);
                codeSequence.add(index, new Load3(var));
            }
        }
        // code sequence updated with insertion of new load and store instructions
    }
}
