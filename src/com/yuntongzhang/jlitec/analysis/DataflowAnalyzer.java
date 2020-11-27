package com.yuntongzhang.jlitec.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.yuntongzhang.jlitec.ir3.Stmt3;

/**
 * A generic dataflow analyzer.
 * Since dataflow value is put in a set, equals and hashcode of dataflow value must be implemented.
 */
public abstract class DataflowAnalyzer<T> {
    private FlowGraph flowGraph;
    private BiFunction<Stmt3, Set<T>, Set<T>> transferFunction;

    private BinaryOperator<Set<T>> meetOperator;
    private Function<BasicBlock, List<Stmt3>> extractMeetCandidates;
    private BiFunction<BasicBlock, Stmt3, Stmt3> getPreviousIns; // function to get previous ins inside a bb
    private Stmt3 startIns; // the analysis start ins to set boundary value on
    private Set<Stmt3> bbStartInsList = new HashSet<>(); // the analysis start ins for each basic block

    // pointer to maps based on whether forward or backward
    private Map<Stmt3, Set<T>> inPointer;
    private Map<Stmt3, Set<T>> outPointer;
    // actual maps holding dataflow values
    protected Map<Stmt3, Set<T>> in = new HashMap<>();
    protected Map<Stmt3, Set<T>> out = new HashMap<>();


    public DataflowAnalyzer(FlowGraph flowGraph, BiFunction<Stmt3, Set<T>, Set<T>> transferFunction,
                            boolean forward, boolean may, Set<T> boundaryValue, Set<T> initValue) {
        this.flowGraph = flowGraph;
        this.transferFunction = transferFunction;
        setInOutPointers(forward);
        setMeetOperator(may);
        setExtractMeetCandidates(forward);
        initAllEntries(initValue);
        setStartInstructions(forward);
        setBoundaryValue(boundaryValue);
        setGetPreviousIns(forward);
    }

    // execute the algorithm to populate results in maps
    public void run() {
        Set<Stmt3> allIns = flowGraph.getAllIns();
        allIns.remove(this.startIns);
        boolean changesMade = true;
        while (changesMade) {
            changesMade = false;
            for (Stmt3 ins : allIns) {
                // get the block that contains this instruction
                BasicBlock currentBb = flowGraph.findBlockWithIns(ins);
                Set<T> inCurrent = null;
                if (bbStartInsList.contains(ins)) {
                    // analysis start ins for some block, need to perform meet
                    List<Stmt3> meetCandidates = extractMeetCandidates.apply(currentBb);
                    inCurrent = meetCandidates.stream().map(s -> outPointer.get(s)).reduce(meetOperator).get();
                } else { // ins is within a block
                    inCurrent = outPointer.get(getPreviousIns.apply(currentBb, ins));
                }
                Set<T> outCurrent = transferFunction.apply(ins, inCurrent);
                // Note: `equals` for data values need to be correctly implemented
                if (!inCurrent.equals(inPointer.get(ins)) || !outCurrent.equals(outPointer.get(ins))) {
                    // obtained new data values
                    changesMade = true;
                }
                inPointer.put(ins, inCurrent);
                outPointer.put(ins, outCurrent);
            }
        }
    }

    public Map<Stmt3, Set<T>> getInMap() {
        return this.in;
    }

    public Map<Stmt3, Set<T>> getOutMap() {
        return this.out;
    }

    private void setInOutPointers(boolean forward) {
        if (forward) {
            this.inPointer = in;
            this.outPointer = out;
        } else {
            this.inPointer = out;
            this.outPointer = in;
        }
    }

    private void setMeetOperator(boolean may) {
        if (may) { // union
            this.meetOperator = (a, b) -> {
                Set<T> resultSet = new HashSet<>(a);
                resultSet.addAll(b);
                return resultSet;
            };
        } else { // intersection
            this.meetOperator = (a, b) -> {
                Set<T> resultSet = new HashSet<>(a);
                resultSet.retainAll(b);
                return resultSet;
            };
        }
    }

    private void setExtractMeetCandidates(boolean forward) {
        // defines how to find the meet candidates
        if (forward) {
            this.extractMeetCandidates =
                    bb -> bb.getPredecessors().stream().map(BasicBlock::getLastIns).collect(Collectors.toList());
        } else {
            this.extractMeetCandidates =
                    bb -> bb.getSuccessors().stream().map(BasicBlock::getFirstIns).collect(Collectors.toList());
        }
    }

    private void initAllEntries(Set<T> initValue) {
        Set<Stmt3> allIns = this.flowGraph.getAllIns();
        for (Stmt3 ins : allIns) {
            this.outPointer.put(ins, initValue);
        }
    }

    private void setStartInstructions(boolean forward) {
        if (forward) {
            this.startIns = this.flowGraph.getFirstIns();
            this.bbStartInsList = this.flowGraph.getLeaders();
        } else {
            this.startIns = this.flowGraph.getLastIns();
            this.bbStartInsList = this.flowGraph.getBackLeaders();
        }
    }

    // overwrite the boundary value to correct one
    // need to run transfer function once, because we are setting on the first ins instead of imaginary block
    private void setBoundaryValue(Set<T> boundaryValue) {
        this.inPointer.put(this.startIns, boundaryValue);
        Set<T> valAfterFirst = this.transferFunction.apply(this.startIns, boundaryValue);
        this.outPointer.put(this.startIns, valAfterFirst);
    }

    private void setGetPreviousIns(boolean forward) {
        if (forward) {
            this.getPreviousIns = BasicBlock::getPreviousIns;
        } else {
            this.getPreviousIns = BasicBlock::getNextIns;
        }
    }
}
