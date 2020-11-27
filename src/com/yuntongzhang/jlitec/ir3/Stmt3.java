package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yuntongzhang.jlitec.analysis.AvailableExpAnalyzer;
import com.yuntongzhang.jlitec.ast.PrettyPrintable;

public abstract class Stmt3 implements PrettyPrintable {
    // for liveness analysis
    public Set<Id3> getDefSet() {
        return new HashSet<>();
    }
    public Set<Id3> getUseSet() {
        return new HashSet<>();
    }

    /**
     * For available expression analysis.
     * Does not return assignment with Id3/literal/Access3 as RHS,
     *      since they are not considered as part of dataflow values;
     * Only Stmt3 which involves assignment needs to implement this method,
     *      except AssignAccess3 which is not considered in the analysis.
     */
    public Set<AvailableExpAnalyzer.Assignment> getAssignment() { return new HashSet<>(); }

    // replace (used) vars in this stmt according to the replacement scheme
    public void replaceVars(Map<Id3, Id3> replacementScheme) {}
}
