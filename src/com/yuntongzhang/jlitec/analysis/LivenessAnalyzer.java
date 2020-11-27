package com.yuntongzhang.jlitec.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import com.yuntongzhang.jlitec.ir3.Id3;
import com.yuntongzhang.jlitec.ir3.Stmt3;


public class LivenessAnalyzer extends DataflowAnalyzer<Id3> {
    private static BiFunction<Stmt3, Set<Id3>, Set<Id3>> transferFunction = (ins, input) -> {
        // get def and use for a particular instruction
        Set<Id3> def = ins.getDefSet();
        Set<Id3> use = ins.getUseSet();
        // formula
        Set<Id3> output = new HashSet<>(input);
        output.removeAll(def);
        output.addAll(use);
        return output;
    };

    public LivenessAnalyzer(FlowGraph flowGraph) {
        super(flowGraph, transferFunction, false, true,
                new HashSet<>(), new HashSet<>());
    }

    public List<Set<Id3>> getLiveRangeOverlaps() {
        List<Set<Id3>> overlaps = new ArrayList<>();
        for (Stmt3 key : in.keySet()) {
            overlaps.add(in.get(key));
        }
        for (Stmt3 key : out.keySet()) {
            overlaps.add(out.get(key));
        }
        return overlaps;
    }

    public Set<Id3> getAllVariablesInMethod() {
        Set<Id3> allVars = new HashSet<>();
        List<Set<Id3>> overlaps = getLiveRangeOverlaps();
        for (Set<Id3> overlap : overlaps) {
            allVars.addAll(overlap);
        }
        return allVars;
    }
}
