package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Set;

public abstract class Exp3 {
    public Set<Id3> getAllVariables() {
        return new HashSet<>();
    }

    // for available expression analysis
    public boolean containsVar(Id3 id3) { return false; }
}
