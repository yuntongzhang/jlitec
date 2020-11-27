package com.yuntongzhang.jlitec.analysis;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

import com.yuntongzhang.jlitec.ir3.Id3;
import com.yuntongzhang.jlitec.ir3.Stmt3;

/**
 * Dataflow value is Stmt3 (more specifically, Stmt3 with non-empty def-set)
 * We dont need to implement `equals` for Stmt3 here, because each Stmt3 should be treated
 * as different, even if they have the same content
 *
 * This analyzer is not used in the current optimizations,
 * but can be useful if more optimizations are to be developed.
 */
public class ReachingDefAnalyzer extends DataflowAnalyzer<Stmt3> {
    private static BiFunction<Stmt3, Set<Stmt3>, Set<Stmt3>> transferFunction = (ins, input) -> {
        Set<Id3> varsDefined = ins.getDefSet();
        if (!varsDefined.isEmpty()) {
            // this ins introduces a new definition, so need to kill same definitions in the input
            Set<Stmt3> killSet = new HashSet<>();
            for (Stmt3 insWithDef : input) {
                if (insWithDef.getDefSet().containsAll(varsDefined)) {
                    // actually def sets should only have one var each
                    killSet.add(insWithDef);
                }
            }
            Set<Stmt3> output = new HashSet<>(input);
            output.removeAll(killSet);
            output.add(ins);
            return output;
        } else {
            // no new definition introduced here, so nothing happens
            return input;
        }
    };

    public ReachingDefAnalyzer(FlowGraph flowGraph) {
        super(flowGraph, transferFunction, true, true,
                new HashSet<>(), new HashSet<>());
    }
}
